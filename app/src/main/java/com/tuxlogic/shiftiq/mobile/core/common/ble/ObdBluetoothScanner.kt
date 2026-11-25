package com.tuxlogic.shiftiq.mobile.core.common.ble

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

data class ScannedObdDevice(
    val id: String,
    val name: String,
    val address: String,
    val rssi: Int?,
    val isRecommended: Boolean
) {
    val signalLabel: String
        get() = when {
            rssi == null -> "Bonded device"
            rssi >= -55 -> "Excellent ($rssi dBm)"
            rssi >= -70 -> "Good ($rssi dBm)"
            rssi >= -85 -> "Fair ($rssi dBm)"
            else -> "Weak ($rssi dBm)"
        }
}

/**
 * Discovers nearby OBD-II adapters via Bluetooth LE plus already-bonded classic devices.
 * Full ELM327 AT-command sessions remain workshop/IoT-server owned; this layer handles
 * local discovery and GATT connectability checks for the driver app.
 */
@Singleton
class ObdBluetoothScanner @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    private val adapter: BluetoothAdapter?
        get() = bluetoothManager?.adapter

    fun hasBluetoothHardware(): Boolean =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ||
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true

    fun missingPermissions(): List<String> {
        val required = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            } else {
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.BLUETOOTH)
                add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }
        return required.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
    }

    fun scan(durationMs: Long = DEFAULT_SCAN_MS): Flow<List<ScannedObdDevice>> = callbackFlow {
        val missing = missingPermissions()
        if (missing.isNotEmpty()) {
            close(SecurityException("Missing Bluetooth permissions: ${missing.joinToString()}"))
            return@callbackFlow
        }
        val bt = adapter
        if (bt == null) {
            close(IllegalStateException("Bluetooth hardware is unavailable on this device."))
            return@callbackFlow
        }
        if (!bt.isEnabled) {
            close(IllegalStateException("Bluetooth is turned off. Enable it to scan for OBD-II adapters."))
            return@callbackFlow
        }

        val discovered = linkedMapOf<String, ScannedObdDevice>()

        @SuppressLint("MissingPermission")
        fun publish() {
            trySend(discovered.values.sortedWith(
                compareByDescending<ScannedObdDevice> { it.isRecommended }
                    .thenByDescending { it.rssi ?: Int.MIN_VALUE }
            ))
        }

        @SuppressLint("MissingPermission")
        fun upsert(device: BluetoothDevice, rssi: Int?) {
            val address = device.address ?: return
            val rawName = runCatching { device.name }.getOrNull().orEmpty().ifBlank { "OBD Adapter" }
            val recommended = looksLikeObd(rawName)
            val existing = discovered[address]
            discovered[address] = ScannedObdDevice(
                id = address,
                name = rawName,
                address = address,
                rssi = rssi ?: existing?.rssi,
                isRecommended = recommended || existing?.isRecommended == true
            )
            publish()
        }

        // Seed with bonded adapters (common for classic ELM327 dongles).
        @SuppressLint("MissingPermission")
        fun seedBonded() {
            runCatching {
                bt.bondedDevices.orEmpty().forEach { device ->
                    val name = runCatching { device.name }.getOrNull().orEmpty()
                    if (looksLikeObd(name) || name.isBlank()) {
                        upsert(device, rssi = null)
                    }
                }
            }
        }
        seedBonded()
        publish()

        val scanner = bt.bluetoothLeScanner
        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                upsert(result.device, result.rssi)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { upsert(it.device, it.rssi) }
            }

            override fun onScanFailed(errorCode: Int) {
                close(IllegalStateException("Bluetooth LE scan failed (code $errorCode)."))
            }
        }

        @SuppressLint("MissingPermission")
        fun startLeScan() {
            if (scanner == null) return
            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build()
            scanner.startScan(null, settings, callback)
        }

        startLeScan()

        val stopJob = launch {
            delay(durationMs)
            if (isActive) close()
        }

        awaitClose {
            stopJob.cancel()
            @SuppressLint("MissingPermission")
            runCatching { scanner?.stopScan(callback) }
        }
    }

    /**
     * Attempts a short GATT connection to confirm the adapter is reachable.
     * Returns true when the remote device accepts the connection attempt.
     */
    @SuppressLint("MissingPermission")
    fun connectProbe(address: String, timeoutMs: Long = 8_000L): Flow<Boolean> = flow {
        if (missingPermissions().isNotEmpty()) {
            emit(false)
            return@flow
        }
        val bt = adapter ?: run {
            emit(false)
            return@flow
        }
        val device = runCatching { bt.getRemoteDevice(address) }.getOrNull() ?: run {
            emit(false)
            return@flow
        }

        var connected = false
        val latch = java.util.concurrent.CountDownLatch(1)
        val gattCallback = object : android.bluetooth.BluetoothGattCallback() {
            override fun onConnectionStateChange(
                gatt: android.bluetooth.BluetoothGatt,
                status: Int,
                newState: Int
            ) {
                if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                    connected = true
                }
                latch.countDown()
                runCatching { gatt.disconnect() }
                runCatching { gatt.close() }
            }
        }
        val gatt = device.connectGatt(context, false, gattCallback)
        val finished = latch.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)
        if (!finished) {
            runCatching { gatt.disconnect() }
            runCatching { gatt.close() }
        }
        emit(connected)
    }

    companion object {
        private const val DEFAULT_SCAN_MS = 12_000L

        private val OBD_NAME_HINTS = listOf(
            "obd", "obdii", "obd2", "elm327", "vgate", "veepeak", "konnwei",
            "carista", "bafx", "viecar", "lexia", "canbus", "scanner"
        )

        fun looksLikeObd(name: String): Boolean {
            val normalized = name.lowercase()
            return OBD_NAME_HINTS.any { normalized.contains(it) }
        }
    }
}
