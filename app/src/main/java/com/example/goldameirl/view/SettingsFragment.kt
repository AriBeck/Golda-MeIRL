package com.example.goldameirl.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.goldameirl.R
import com.example.goldameirl.misc.INTERVAL_KEY
import com.example.goldameirl.misc.RADIUS_KEY

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference(RADIUS_KEY)
        radiusPreference?.apply {
            summary = this.value.times(100).toString() + " Meters"

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(100).toString() + " Meters"
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(INTERVAL_KEY)
        timePreference?.apply {
            summary = this.value.times(5).toString() + " Minutes(s)"

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(5).toString() + " Minute(s)"
                true
            }
        }
    }
}