package com.example.goldameirl.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.goldameirl.R


class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference("radius")
        radiusPreference?.apply {
            summary = this.value.times(100).toString() + " Meters"

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(100).toString() + " Meters"
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference("time")
        timePreference?.apply {
            summary = this.value.times(5).toString() + " Minutes(s)"

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(5).toString() + " Minute(s)"
                true
            }
        }
    }
}