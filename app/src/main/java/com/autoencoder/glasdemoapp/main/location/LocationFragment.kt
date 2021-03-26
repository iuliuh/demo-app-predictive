package com.autoencoder.glasdemoapp.main.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.mapbox.android.core.location.*
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationUpdate
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.autoencoder.glasdemoapp.BR
import com.autoencoder.glasdemoapp.R
import com.autoencoder.glasdemoapp.databinding.LocationFragmentBinding
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_INTERVAL_IN_MILLISECONDS
import com.autoencoder.glasdemoapp.main.headingInformation.DEFAULT_MAX_WAIT_TIME
import com.autoencoder.glasdemoapp.models.Location
import com.autoencoder.glasdemoapp.shared.base.BaseFragment
import com.autoencoder.glasdemoapp.shared.utils.extensions.toast
import com.autoencoder.glasdemoapp.shared.utils.generateBitmap
import com.tbruyelle.rxpermissions2.RxPermissions

const val ZOOM_LEVEL = 12.0

const val ICON_ID = "ICON_ID"
const val SOURCE_ID = "SOURCE_ID"
const val LAYER_ID = "LAYER_ID"

abstract class LocationFragment : BaseFragment<LocationFragmentBinding>(true) {

    protected lateinit var mapView: MapView
    protected lateinit var mapboxMap: MapboxMap
    protected lateinit var locationEngine: LocationEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context?.let { context ->
            Mapbox.getInstance(context, getString(R.string.mapbox_public_token))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LocationFragmentBinding.inflate(layoutInflater).also {
        it.lifecycleOwner = viewLifecycleOwner
        it.setVariable(BR.viewModel, viewModel)
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding?.gasStationMap?.let {
            it.onCreate(savedInstanceState)
            mapView = it
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupViews() {
        requestPermission()
    }

    private fun setupMap() {
        mapView.getMapAsync { mapboxMap ->
            this.mapboxMap = mapboxMap
            setupObservers()
        }
    }

    abstract fun setupObservers()

    @SuppressLint("CheckResult")
    private fun requestPermission() {
        RxPermissions(this)
            .requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe {
                if (it.granted) {
                    setupMap()
                }
            }
    }

    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        context?.let { context ->
            mapboxMap.locationComponent.apply {
                activateLocationComponent(
                    LocationComponentActivationOptions.builder(context, style)
                        .useDefaultLocationEngine(false)
                        .build()
                )
                isLocationComponentEnabled = true
                cameraMode = CameraMode.TRACKING
                renderMode = RenderMode.COMPASS
            }
            initLocationEngine(context)
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocationEngine(context: Context) {
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()

        with(LocationEngineProvider.getBestLocationEngine(context)) {
            locationEngine = this
            requestLocationUpdates(request, locationEngineCallback, Looper.getMainLooper())
            getLastLocation(locationEngineCallback)
        }
    }

    protected fun placeMarker(location: Location) {
        val view = ImageView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setImageResource(R.drawable.ic_map_marker)
        }
        val symbolLayerIconFeatureList = Feature.fromGeometry(
            Point.fromLngLat(
                location.longitude.toDouble(),
                location.latitude.toDouble()
            )
        )
        mapboxMap.setStyle(
            Style.Builder().fromUri(Style.MAPBOX_STREETS)
                .withImage(ICON_ID, view.generateBitmap())
                .withSource(
                    GeoJsonSource(
                        SOURCE_ID,
                        FeatureCollection.fromFeature(symbolLayerIconFeatureList)
                    )
                )
                .withLayer(
                    SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                            iconImage(ICON_ID),
                            iconAllowOverlap(true),
                            iconIgnorePlacement(true)
                        )
                )
        ) {
            enableLocationComponent(it)
        }
    }

    private val locationEngineCallback = object : LocationEngineCallback<LocationEngineResult> {
        override fun onSuccess(result: LocationEngineResult?) {
            result?.lastLocation?.let { lastLocation ->
                with(mapboxMap.locationComponent) {
                    zoomWhileTracking(ZOOM_LEVEL, 0)
                    forceLocationUpdate(
                        LocationUpdate.Builder().location(lastLocation).build()
                    )
                }
            }
        }

        override fun onFailure(exception: Exception) {
            toast(exception.localizedMessage ?: exception.stackTraceToString())
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}