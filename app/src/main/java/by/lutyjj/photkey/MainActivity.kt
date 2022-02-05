package by.lutyjj.photkey

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import by.lutyjj.photkey.api.PhotKeyApi
import by.lutyjj.photkey.fragments.GalleryFragment
import by.lutyjj.photkey.fragments.SettingsFragment
import by.lutyjj.photkey.services.SyncService
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this

        getApiPath()?.let { PhotKeyApi.initRetrofitService(it) }

        val firstFragment = GalleryFragment()
        val secondFragment = SettingsFragment()

        setCurrentFragment(firstFragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.gallery -> setCurrentFragment(firstFragment)
                R.id.settings -> setCurrentFragment(secondFragment)
            }
            true
        }

        startService(Intent(applicationContext, SyncService::class.java))
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    companion object SettingsHelper {
        private lateinit var instance: MainActivity

        fun getApiPath(): String? {
            val preferences = PreferenceManager.getDefaultSharedPreferences(instance)
            return preferences.getString("api_path", "http://10.0.2.2:8080/api/")
        }
    }
}