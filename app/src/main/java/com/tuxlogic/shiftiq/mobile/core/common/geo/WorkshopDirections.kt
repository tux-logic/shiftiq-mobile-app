package com.tuxlogic.shiftiq.mobile.core.common.geo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Opens turn-by-turn directions to a workshop using coordinates when available,
 * otherwise falls back to a Google Maps search by address.
 */
fun openWorkshopDirections(
    context: Context,
    address: String,
    latitude: Double? = null,
    longitude: Double? = null,
    label: String = "Taller ShiftIQ"
) {
    val uri = when {
        latitude != null && longitude != null -> {
            Uri.parse(
                "google.navigation:q=$latitude,$longitude&mode=d"
            )
        }
        address.isNotBlank() -> {
            Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=${Uri.encode(address)}"
            )
        }
        else -> null
    }

    if (uri == null) {
        Toast.makeText(context, "No hay dirección disponible para navegar.", Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (latitude != null && longitude != null) {
            // Prefer Google Maps navigation when coords exist; fallback below.
            setPackage("com.google.android.apps.maps")
        }
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val webUri = if (latitude != null && longitude != null) {
            Uri.parse(
                "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude&destination_place_id=&travelmode=driving"
            )
        } else {
            Uri.parse("https://www.google.com/maps/search/?api=1&query=${Uri.encode(address.ifBlank { label })}")
        }
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}
