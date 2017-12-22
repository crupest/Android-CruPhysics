package crupest.cruphysics

import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment

class WorldSettingsActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_settings)

        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.content, SettingsFragment())
        transaction.commit()

        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.world_preference)
        }
    }
}
