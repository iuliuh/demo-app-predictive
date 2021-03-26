package com.autoencoder.glasdemoapp.main.multipleLocations.gasStations

import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsFragment
import com.autoencoder.glasdemoapp.models.Location
import org.koin.androidx.viewmodel.ext.android.viewModel

class GasStationsFragment : MultipleLocationsFragment() {

    override val viewModel by viewModel<GasStationsViewModel>()

    override fun onLocationReady(location: Location) {
        viewModel.requestMarkerLocation(location)
    }

    override fun setupObservers() {
        viewModel.markers.observe(viewLifecycleOwner, ::placeMarkers)
    }

    override fun onPause() {
        super.onPause()
        viewModel.unregisterListeners()
    }
}