package com.tuxlogic.shiftiq.mobile.core.designsystem.components

import android.graphics.Color as AndroidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory.circleColor
import org.maplibre.android.style.layers.PropertyFactory.circleOpacity
import org.maplibre.android.style.layers.PropertyFactory.circleRadius
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeColor
import org.maplibre.android.style.layers.PropertyFactory.circleStrokeWidth
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point

/**
 * MapLibre OpenFreeMap Positron map matching Flutter [MinimalistMapConfig] /
 * [BranchMapView] (bundled `assets/map_styles/shiftiq_positron.json`).
 */
@Composable
fun ShiftIQPositronMap(
    pins: List<ShiftIQMapPin>,
    modifier: Modifier = Modifier,
    selectedPinId: String? = pins.firstOrNull()?.id,
    onPinClick: (String) -> Unit = {},
    onMapReady: (MapLibreMapController) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply { onCreate(null) }
    }
    val controller = remember { MapLibreMapController() }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                getMapAsync { map ->
                    controller.attach(map, mapView)
                    map.uiSettings.isAttributionEnabled = false
                    map.uiSettings.isLogoEnabled = false
                    map.uiSettings.isCompassEnabled = false
                    map.setStyle(Style.Builder().fromUri(MapLibreMapController.POSITRON_STYLE_URI)) { style ->
                        controller.bindStyle(style)
                        controller.renderPins(pins, selectedPinId)
                        controller.fitPins(pins)
                        map.addOnMapClickListener { point ->
                            val id = controller.nearestPinId(point, pins)
                            if (id != null) {
                                onPinClick(id)
                                true
                            } else {
                                false
                            }
                        }
                        onMapReady(controller)
                    }
                }
            }
        },
        update = {
            controller.renderPins(pins, selectedPinId)
        }
    )
}

data class ShiftIQMapPin(
    val id: String,
    val latitude: Double,
    val longitude: Double
)

class MapLibreMapController {
    private var map: org.maplibre.android.maps.MapLibreMap? = null
    private var mapView: MapView? = null
    private var style: Style? = null

    fun attach(map: org.maplibre.android.maps.MapLibreMap, mapView: MapView) {
        this.map = map
        this.mapView = mapView
    }

    fun bindStyle(style: Style) {
        this.style = style
        if (style.getSource(SOURCE_ID) == null) {
            style.addSource(GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(emptyArray())))
        }
        if (style.getLayer(LAYER_SELECTED) == null) {
            style.addLayer(
                CircleLayer(LAYER_SELECTED, SOURCE_ID)
                    .withFilter(
                        org.maplibre.android.style.expressions.Expression.eq(
                            org.maplibre.android.style.expressions.Expression.get("selected"),
                            org.maplibre.android.style.expressions.Expression.literal(true)
                        )
                    )
                    .withProperties(
                        circleRadius(9f),
                        circleColor(AndroidColor.parseColor("#1E3A8A")),
                        circleStrokeColor(AndroidColor.WHITE),
                        circleStrokeWidth(2f),
                        circleOpacity(0.95f)
                    )
            )
        }
        if (style.getLayer(LAYER_DEFAULT) == null) {
            style.addLayer(
                CircleLayer(LAYER_DEFAULT, SOURCE_ID)
                    .withFilter(
                        org.maplibre.android.style.expressions.Expression.eq(
                            org.maplibre.android.style.expressions.Expression.get("selected"),
                            org.maplibre.android.style.expressions.Expression.literal(false)
                        )
                    )
                    .withProperties(
                        circleRadius(6.5f),
                        circleColor(AndroidColor.parseColor("#0F172A")),
                        circleStrokeColor(AndroidColor.WHITE),
                        circleStrokeWidth(2f),
                        circleOpacity(0.95f)
                    )
            )
        }
    }

    fun renderPins(pins: List<ShiftIQMapPin>, selectedPinId: String?) {
        val geoSource = style?.getSourceAs<GeoJsonSource>(SOURCE_ID) ?: return
        val features = pins.map { pin ->
            Feature.fromGeometry(Point.fromLngLat(pin.longitude, pin.latitude)).also { feature ->
                feature.addStringProperty("id", pin.id)
                feature.addBooleanProperty("selected", pin.id == selectedPinId)
            }
        }
        geoSource.setGeoJson(FeatureCollection.fromFeatures(features))
    }

    fun fitPins(pins: List<ShiftIQMapPin>) {
        val mapLibreMap = map ?: return
        if (pins.isEmpty()) {
            mapLibreMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(DEFAULT_CENTER, DEFAULT_ZOOM)
            )
            return
        }
        if (pins.size == 1) {
            mapLibreMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(pins.first().latitude, pins.first().longitude),
                    13.5
                )
            )
            return
        }
        val bounds = LatLngBounds.Builder().apply {
            pins.forEach { include(LatLng(it.latitude, it.longitude)) }
        }.build()
        mapLibreMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 72))
    }

    fun zoomBy(delta: Double) {
        map?.animateCamera(CameraUpdateFactory.zoomBy(delta))
    }

    fun recenter(pins: List<ShiftIQMapPin>) {
        fitPins(pins)
    }

    fun nearestPinId(point: LatLng, pins: List<ShiftIQMapPin>, maxDegrees: Double = 0.01): String? {
        return pins.minByOrNull { pin ->
            val dLat = pin.latitude - point.latitude
            val dLng = pin.longitude - point.longitude
            dLat * dLat + dLng * dLng
        }?.takeIf { pin ->
            val dLat = pin.latitude - point.latitude
            val dLng = pin.longitude - point.longitude
            dLat * dLat + dLng * dLng <= maxDegrees * maxDegrees
        }?.id
    }

    companion object {
        private const val SOURCE_ID = "shiftiq-workshop-pins"
        private const val LAYER_SELECTED = "shiftiq-workshop-pins-selected"
        private const val LAYER_DEFAULT = "shiftiq-workshop-pins-default"
        val DEFAULT_CENTER = LatLng(-12.0865, -76.9905)
        const val DEFAULT_ZOOM = 12.0
        const val POSITRON_STYLE_URI = "asset://map_styles/shiftiq_positron.json"
    }
}

/**
 * Deterministic Lima-area coordinates when the backend does not yet expose lat/lng.
 * Mirrors Flutter placeholder pin spread around the default OpenFreeMap center.
 */
fun workshopMapPins(workshopIds: List<String>): List<ShiftIQMapPin> {
    val offsets = listOf(
        0.000 to 0.000,
        0.012 to -0.018,
        -0.015 to 0.022,
        0.008 to 0.014,
        -0.010 to -0.012,
        0.018 to 0.006,
        -0.006 to 0.020,
        0.014 to -0.008
    )
    val center = MapLibreMapController.DEFAULT_CENTER
    return workshopIds.mapIndexed { index, id ->
        val offset = offsets[index % offsets.size]
        ShiftIQMapPin(
            id = id,
            latitude = center.latitude + offset.first,
            longitude = center.longitude + offset.second
        )
    }
}
