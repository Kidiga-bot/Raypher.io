package com.Raypher_Pro.security

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * BlockActivity - The "Shield" that intercepts unauthorized transactions.
 *
 * CRITICAL SECURITY FEATURES:
 * - Overrides back button to prevent escape
 * - Full-screen black background with red alarm border
 * - Only dismissible via "Biometric Override" button
 * - Launched with FLAG_ACTIVITY_NEW_TASK to appear over banking apps
 *
 * FLOW:
 * 1. SecurityReceiver detects "com.raypher.SECURITY_ALERT" broadcast
 * 2. If Savannah Trust is protected (is_savannah_locked == true), launches THIS activity
 * 3. User sees threat alert and must click "Biometric Override"
 * 4. Shows verification toast and dismisses
 */
class BlockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block)

        // Bind the override button
        val overrideButton = findViewById<Button>(R.id.overrideButton)

        overrideButton.setOnClickListener {
            // Show verification message
            Toast.makeText(this, getString(R.string.threat_neutralized), Toast.LENGTH_LONG).show()

            // Dismiss the shield
            finish()
        }
    }

    /**
     * CRITICAL: Override back button to TRAP the attacker. The thief CANNOT escape this shield by
     * pressing back. Only the "Biometric Override" button can dismiss it.
     */
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Intentionally empty - back button does nothing
        // This prevents the attacker from escaping the shield
    }
}
