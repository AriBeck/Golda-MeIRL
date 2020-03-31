package com.example.goldameirl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import com.example.goldameirl.databinding.FragmentMapBinding
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style

/**
 * A simple [Fragment] subclass.
 */
class MapFragment : Fragment() {

    lateinit var mapView: MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Mapbox.getInstance(this.context!!, "pk.eyJ1IjoiYXJpYmVjazUyIiwiYSI6ImNrOGZwa2ZveTAxdzQzbG4yOTl5ajZhOWgifQ.Ht_h6D6yCqdRTJohKF0nJA");
        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater,
            R.layout.fragment_map,container,false)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.MAPBOX_STREETS) {

                // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
            }
        }
        return binding.root
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }


}
