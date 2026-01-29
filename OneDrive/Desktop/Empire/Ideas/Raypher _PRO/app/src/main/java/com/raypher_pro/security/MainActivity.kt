package com.Raypher_Pro.security

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * MainActivity - The "Cyberpunk Command Center"
 *
 * MISSION: Display the security dashboard and control the Active Interceptor.
 *
 * FEATURES:
 * 1. Shows secured apps (WhatsApp, Gmail)
 * 2. FAB button "scans" for vulnerable apps
 * 3. Reveals Savannah Trust card with protection toggle
 * 4. Toggle saves to SharedPreferences "RaypherPrefs"
 * 5. SecurityReceiver reads the same SharedPreferences to activate the Shield
 *
 * CRITICAL: Uses SharedPreferences name "RaypherPrefs" to match SecurityReceiver.
 */
class MainActivity : AppCompatActivity() {

    companion object {
        // SharedPreferences file name - MUST match SecurityReceiver
        private const val PREFS_NAME = "RaypherPrefs"

        // Key for Savannah Trust protection state
        private const val KEY_SAVANNAH_LOCKED = "is_savannah_locked"
    }

    private lateinit var cardSavannah: CardView
    private lateinit var switchSavannah: SwitchMaterial
    private lateinit var fabScan: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        cardSavannah = findViewById(R.id.cardSavannah)
        switchSavannah = findViewById(R.id.switchSavannah)
        fabScan = findViewById(R.id.fabScan)

        // Initialize: Hide Savannah Trust card (revealed after scan)
        cardSavannah.visibility = View.GONE

        // Load saved protection state from SharedPreferences
        loadSavannahProtectionState()

        // FAB Click: Simulate scanning for vulnerable apps
        fabScan.setOnClickListener { performScan() }

        // Switch Toggle: Enable/Disable Savannah Trust protection
        switchSavannah.setOnCheckedChangeListener { _, isChecked ->
            saveProtectionState(isChecked)

            if (isChecked) {
                Toast.makeText(this, "⚡ Interceptor Armed.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⚠️ Protection Disabled.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Simulate scanning for vulnerable banking apps. Shows a progress dialog for 2 seconds, then
     * reveals the Savannah Trust card.
     */
    @Suppress("DEPRECATION")
    private fun performScan() {
        // Show scanning progress dialog
        val progressDialog =
                ProgressDialog(this).apply {
                    setMessage("Scanning for vulnerable apps...")
                    setCancelable(false)
                    show()
                }

        // Simulate 2-second scan
        Handler(Looper.getMainLooper())
                .postDelayed(
                        {
                            progressDialog.dismiss()

                            // Reveal Savannah Trust card
                            cardSavannah.visibility = View.VISIBLE

                            Toast.makeText(
                                            this,
                                            "🔍 Threat Detected: Savannah Trust Mobile",
                                            Toast.LENGTH_LONG
                                    )
                                    .show()
                        },
                        2000
                )
    }

    /**
     * Save the protection state to SharedPreferences. CRITICAL: Uses "RaypherPrefs" to match
     * SecurityReceiver.
     */
    private fun saveProtectionState(isLocked: Boolean) {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_SAVANNAH_LOCKED, isLocked).apply()
    }

    /**
     * Load the saved protection state from SharedPreferences. Sets the switch to the saved state.
     */
    private fun loadSavannahProtectionState() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLocked = prefs.getBoolean(KEY_SAVANNAH_LOCKED, false)

        // Set switch to saved state (without triggering listener)
        switchSavannah.isChecked = isLocked
    }
}
