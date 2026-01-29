package com.Raypher_Pro.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * SecurityReceiver - The "Ear" that listens for threat broadcasts.
 *
 * CRITICAL ARCHITECTURE:
 * - Listens for broadcast action: "com.raypher.SECURITY_ALERT"
 * - Checks SharedPreferences to see if Savannah Trust is protected
 * - If protected (is_savannah_locked == true), launches BlockActivity
 * - Launched with FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP
 *
 * FLOW:
 * 1. Savannah Trust app broadcasts "com.raypher.SECURITY_ALERT" when theft is attempted
 * 2. THIS receiver wakes up
 * 3. Checks if user has enabled protection for Savannah Trust
 * 4. If YES -> Launch BlockActivity to intercept
 * 5. If NO -> Do nothing (let the theft happen as a demo)
 *
 * CRITICAL: Uses SharedPreferences name "RaypherPrefs" to match MainActivity's persistence layer.
 * This ensures the protection toggle state is consistent across the app.
 */
class SecurityReceiver : BroadcastReceiver() {

    companion object {
        // SharedPreferences file name - MUST match MainActivity
        private const val PREFS_NAME = "RaypherPrefs"

        // Key for Savannah Trust protection state
        private const val KEY_SAVANNAH_LOCKED = "is_savannah_locked"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Validate context
        context ?: return

        // Get SharedPreferences (same file as MainActivity uses)
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Check if Savannah Trust is protected
        val isSavannahLocked = prefs.getBoolean(KEY_SAVANNAH_LOCKED, false)

        if (isSavannahLocked) {
            // INTERCEPT: Launch the blocking shield
            val blockIntent =
                    Intent(context, BlockActivity::class.java).apply {
                        // CRITICAL FLAGS:
                        // - FLAG_ACTIVITY_NEW_TASK: Required to launch from broadcast receiver
                        // - FLAG_ACTIVITY_CLEAR_TOP: Ensure single instance of BlockActivity
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }

            context.startActivity(blockIntent)
        }
        // If not locked, do nothing (allow the theft as a demo)
    }
}
