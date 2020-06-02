package com.example.goldameirl.view

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.example.goldameirl.R
import com.example.goldameirl.misc.INTERVAL_KEY
import com.example.goldameirl.misc.INTERVAL_MULTIPLIER
import com.example.goldameirl.misc.RADIUS_KEY
import com.example.goldameirl.misc.RADIUS_MULTIPLIER

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_settings, rootKey)

        val radiusPreference: SeekBarPreference? = findPreference(RADIUS_KEY)
        radiusPreference?.apply {
            summary = this.value.times(RADIUS_MULTIPLIER).toString() + getString(R.string.meters)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).times(RADIUS_MULTIPLIER).toString() +
                        getString(R.string.meters)
                true
            }
        }

        val timePreference: SeekBarPreference? = findPreference(INTERVAL_KEY)
        timePreference?.apply {
            summary = this.value.toLong().times(INTERVAL_MULTIPLIER).toString() + getString(R.string.minutes)

            setOnPreferenceChangeListener { preference, newValue ->
                preference.summary = (newValue as Int).toLong()
                    .times(INTERVAL_MULTIPLIER).toString() +
                        getString(R.string.minutes)
                true
            }
        }
    }
}