package by.lutyjj.photkey.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import by.lutyjj.photkey.R
import by.lutyjj.photkey.api.PhotKeyApi


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val pref: Preference? = findPreference("api_path")
        pref?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                PhotKeyApi.initRetrofitService(newValue as String)
                true
            }
    }
}