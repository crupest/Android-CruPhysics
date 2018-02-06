package crupest.cruphysics

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.support.v7.app.AppCompatActivity
import crupest.cruphysics.physics.serialization.JsonParser
import crupest.cruphysics.preference.Vector2Preference
import org.dyn4j.geometry.Vector2

class WorldSettingsActivity : AppCompatActivity() {

    companion object {
        const val RESULT_GRAVITY = "GRAVITY"
    }

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
        private val json = JsonParser()

        private fun Vector2.toJson(): String = json.moshi.adapter(Vector2::class.java).toJson(this)
        private fun String.toVector2(): Vector2 = json.moshi.adapter(Vector2::class.java).fromJson(this)!!

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.world_preference)
            val gravityPreference = findPreference("gravity") as Vector2Preference
            gravityPreference.vector = activity.intent.getStringExtra(MainActivity.ARG_GRAVITY).toVector2()
            gravityPreference.setOnPreferenceChangeListener { _, newValue ->
                val result = Intent()
                result.putExtra(WorldSettingsActivity.RESULT_GRAVITY, (newValue as Vector2).toJson())
                activity.setResult(Activity.RESULT_OK, result)
                true
            }
        }
    }
}
