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
import com.example.goldameirl.misc.*
import com.example.goldameirl.viewmodel.MapViewModel
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
    private var toggleList: MutableList<Switch> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        mainActivity = requireActivity() as MainActivity
        application = requireActivity().application
        Mapbox.getInstance(application, TOKEN)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_map, container, false
        )

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
            .getInstance(application)).get(MapViewModel::class.java)

        binding.viewModel = viewModel
        mapView = binding.mapView
        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync(viewModel)

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        observeViewModel()
        initButtons()
        initToggles()

        return binding.root
    }

    private fun observeViewModel() {
        viewModel.isMapReady.observe(viewLifecycleOwner, Observer {
            if (it) {
                viewModel.onMapLoaded()
                setupAnnotations()
                setLayerToggleListeners()
            }
        })

        viewModel.shouldNavigateToAlerts.observe(viewLifecycleOwner, Observer { toAlerts ->
            if (toAlerts) {
                this.findNavController().navigate(
                    MapFragmentDirections
                        .mapFragmentToAlertsFragment()
                )
                viewModel.doneNavigatingToAlerts()
            }
        })
    }

    private fun initButtons() {
        binding.menuButton.setOnClickListener {
            mainActivity.binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun initToggles() {
        if (toggleList.isEmpty()) {
            branchToggle = mainActivity.binding.navView.menu
                .findItem(R.id.branch_layer_item).actionView.findViewById(R.id.item_switch)

            branchToggle.tag = BRANCH_LAYER_ID

            anitaToggle = mainActivity.binding.navView.menu
                .findItem(R.id.anita_layer_item).actionView.findViewById(R.id.item_switch)

            anitaToggle.tag = ANITA_LAYER_ID

            toggleList.add(branchToggle)
            toggleList.add(anitaToggle)

            toggleList.forEach {
                it.isChecked = preferences.getBoolean(it.tag as String, true)
            }
        }
    }

    private fun setLayerToggleListeners() {
        toggleList.forEach {
            it.setOnCheckedChangeListener { toggle, isChecked ->
                viewModel.onToggleCheckedChanged(toggle, isChecked)
                preferences.edit().putBoolean(it.tag as String, isChecked).apply()
            }
        }
    }

    private fun setupAnnotations() {
        toggleList.forEach {
            if (it.isChecked) {
                viewModel.addLayer(it.tag as String)
            }
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
