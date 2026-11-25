package com.tuxlogic.shiftiq.mobile.core.common.geo

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class GeoCoordinates(
    val latitude: Double,
    val longitude: Double
)

/**
 * Resolves addresses to coordinates via OpenStreetMap Nominatim when the
 * backend branch catalog does not yet expose latitude/longitude.
 */
@Singleton
class AddressGeocoder @Inject constructor(
    private val gson: Gson
) {
    private val cache = mutableMapOf<String, GeoCoordinates?>()
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun geocode(address: String): GeoCoordinates? = withContext(Dispatchers.IO) {
        val key = address.trim().lowercase()
        if (key.isBlank()) return@withContext null
        if (cache.containsKey(key)) return@withContext cache[key]

        // Nominatim fair-use: max ~1 request/second.
        delay(1_100)
        val url = buildString {
            append("https://nominatim.openstreetmap.org/search?format=json&limit=1&q=")
            append(java.net.URLEncoder.encode(address, Charsets.UTF_8.name()))
        }
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "ShiftIQMobile/1.0 (com.tuxlogic.shiftiq.mobile)")
            .header("Accept", "application/json")
            .get()
            .build()

        val coordinates = runCatching {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@use null
                val body = response.body?.string().orEmpty()
                val parsed = gson.fromJson(body, Array<NominatimHit>::class.java)
                parsed.firstOrNull()?.let { hit ->
                    val lat = hit.lat?.toDoubleOrNull()
                    val lon = hit.lon?.toDoubleOrNull()
                    if (lat != null && lon != null) GeoCoordinates(lat, lon) else null
                }
            }
        }.getOrNull()

        cache[key] = coordinates
        coordinates
    }

    private data class NominatimHit(
        @SerializedName("lat") val lat: String? = null,
        @SerializedName("lon") val lon: String? = null
    )
}
