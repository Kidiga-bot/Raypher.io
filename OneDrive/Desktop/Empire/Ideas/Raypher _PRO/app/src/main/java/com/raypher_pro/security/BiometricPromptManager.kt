package com.Raypher_Pro.security

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.security.Signature
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Manages biometric authentication UI and callbacks via AndroidX Biometric library.
 *
 * Features:
 * - STRONG biometric enforcement (Class 3 only)
 * - Flow-based reactive API
 * - Signature-based CryptoObject for hardware key binding
 * - Comprehensive error handling
 *
 * Requires FragmentActivity context for BiometricPrompt display.
 */
class BiometricPromptManager(private val activity: FragmentActivity) {

    companion object {
        private const val TAG = "BiometricPromptManager"
    }

    private val keyManager = RaypherKeyManager()

    /**
     * Trigger biometric authentication with hardware-bound signature.
     *
     * Returns:
     * - Flow<Result<BiometricPrompt.CryptoObject>>
     * - Success: CryptoObject contains initialized Signature
     * - Failure: Authentication error
     */
    fun authenticate(): Flow<Result<BiometricPrompt.CryptoObject>> = callbackFlow {
        // Verify biometric hardware availability
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.i(TAG, "✓ STRONG biometric available")
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                trySend(Result.failure(Exception("No biometric hardware")))
                close()
                return@callbackFlow
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                trySend(Result.failure(Exception("Biometric hardware unavailable")))
                close()
                return@callbackFlow
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                trySend(Result.failure(Exception("No biometrics enrolled")))
                close()
                return@callbackFlow
            }
        }

        // Initialize Signature with hardware-backed private key
        val signature =
                Signature.getInstance("SHA256withECDSA").apply {
                    initSign(keyManager.getPrivateKey())
                }

        val cryptoObject = BiometricPrompt.CryptoObject(signature)

        // BiometricPrompt callback
        val callback =
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(
                            result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        Log.i(TAG, "✓ Biometric authentication SUCCESS")

                        val cryptoObject =
                                result.cryptoObject
                                        ?: return trySend(
                                                        Result.failure(
                                                                Exception("CryptoObject is null")
                                                        )
                                                )
                                                .let {}

                        trySend(Result.success(cryptoObject))
                        close()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Log.w(TAG, "Biometric authentication FAILED (wrong biometric)")
                        // Don't close - user can retry
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)

                        val errorMessage =
                                when (errorCode) {
                                    BiometricPrompt.ERROR_CANCELED -> "User canceled"
                                    BiometricPrompt.ERROR_LOCKOUT ->
                                            "Too many attempts - locked out"
                                    BiometricPrompt.ERROR_LOCKOUT_PERMANENT ->
                                            "Permanently locked out"
                                    BiometricPrompt.ERROR_NO_BIOMETRICS -> "No biometrics enrolled"
                                    BiometricPrompt.ERROR_USER_CANCELED -> "User canceled"
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> "User clicked cancel"
                                    else -> "Error $errorCode: $errString"
                                }

                        Log.e(TAG, "Biometric ERROR: $errorMessage")
                        trySend(Result.failure(Exception(errorMessage)))
                        close()
                    }
                }

        // Build BiometricPrompt UI
        val executor = ContextCompat.getMainExecutor(activity)
        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo =
                BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Raypher Pro Authentication")
                        .setSubtitle("Verify your identity")
                        .setDescription("Hardware-backed biometric authentication required")
                        .setNegativeButtonText("Cancel")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                        .build()

        // Show biometric prompt
        biometricPrompt.authenticate(promptInfo, cryptoObject)

        awaitClose {
            // Cleanup when flow is canceled
            Log.i(TAG, "BiometricPrompt flow closed")
        }
    }
}
