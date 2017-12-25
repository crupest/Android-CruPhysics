package crupest.cruphysics

import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import crupest.cruphysics.physics.WorldManager
import crupest.cruphysics.preference.Vector2Preference
import org.dyn4j.geometry.Vector2

class WorldSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_settings)

        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.content, SettingsFragment())
        transaction.commit()

        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.world_preference)
            val gravityPreference = findPreference("gravity") as Vector2Preference
            gravityPreference.vector = WorldManager.world.gravity
            gravityPreference.setOnPreferenceChangeListener { _, newValue ->
                WorldManager.world.gravity.set(newValue as Vector2)
                true
            }
        }
    }
}
