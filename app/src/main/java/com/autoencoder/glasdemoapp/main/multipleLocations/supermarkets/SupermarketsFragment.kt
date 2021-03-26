package com.autoencoder.glasdemoapp.main.multipleLocations.supermarkets

import com.autoencoder.glasdemoapp.main.multipleLocations.MultipleLocationsFragment
import com.autoencoder.glasdemoapp.models.Location
import org.koin.androidx.viewmodel.ext.android.viewModel

class SupermarketsFragment : MultipleLocationsFragment() {

    override val viewModel by viewModel<SupermarketsViewModel>()

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