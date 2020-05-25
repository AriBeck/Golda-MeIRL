package com.example.goldameirl.view

import android.app.Application
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.goldameirl.R
import com.example.goldameirl.databinding.FragmentMapBinding
import com.example.goldameirl.misc.ANITA_LAYER_ID
import com.example.goldameirl.misc.TOKEN
import com.example.goldameirl.viewmodel.MapViewModel
import com.example.goldameirl.viewmodel.MapViewModelFactory
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView

class MapFragment : Fragment(){
    private lateinit var application: Application
    private lateinit var mainActivity: MainActivity
    lateinit var viewModel: MapViewModel
    lateinit var binding: FragmentMapBinding
    private lateinit var preferences: SharedPreferences
    private var mapView: MapView? = null
    private lateinit var branchToggle: Switch
    private lateinit var anitaToggle: Switch

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mainActivity = requireActivity() as MainActivity
        application = requireActivity().application
        Mapbox.getInstance(application, TOKEN)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map, container, false
        )

        viewModel = ViewModelProvider(
            this, MapViewModelFactory(
                application
            )
        ).get(MapViewModel::class.java)

        binding.viewModel = viewModel
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(viewModel)

        observeViewModel()
        initButtons()
        initToggles()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.mapReady.observe(viewLifecycleOwner, Observer {
            if (it) {
                setupAnnotations()
                setLayerToggleListeners()
            }
        })

        viewModel.toAlerts.observe(viewLifecycleOwner, Observer { toAlerts ->
            if (toAlerts) {
                this.findNavController().navigate(
                    MapFragmentDirections
                        .mapFragmentToAlertsFragment()
                )
                viewModel.onAlertsClicked()
            }
        })
    }

    private fun initButtons() {
        binding.menuButton.setOnClickListener {
            mainActivity.binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initToggles() {
        branchToggle = mainActivity.binding.navView.menu
            .findItem(R.id.branch_layer_item).actionView.findViewById(R.id.item_switch)

        anitaToggle = mainActivity.binding.navView.menu
            .findItem(R.id.anita_layer_item).actionView.findViewById(R.id.item_switch)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)
        branchToggle.isChecked = preferences.getBoolean("branchToggle", true)
        anitaToggle.isChecked = preferences.getBoolean("anitaToggle", true)
    }

    private fun setLayerToggleListeners() {
        branchToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.addGoldaMarkers()
            } else {
                viewModel.removeGoldaMarkers()
            }

            preferences.edit().putBoolean("branchToggle", isChecked).apply()
        }

        anitaToggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.addLayer(ANITA_LAYER_ID)
            } else {
                viewModel.removeLayer(ANITA_LAYER_ID)
            }

            preferences.edit().putBoolean("anitaToggle", isChecked).apply()
        }
    }

    private fun setupAnnotations() {
        if (branchToggle.isChecked) {
            viewModel.addGoldaMarkers()
        }
        if (anitaToggle.isChecked) {
            viewModel.addLayer(ANITA_LAYER_ID)
        }
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}
