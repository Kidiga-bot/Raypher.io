package com.Raypher_Pro.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature

/**
 * Manages hardware-backed cryptographic keys in AndroidKeyStore.
 *
 * Key Features:
 * - EC (secp256r1) key generation
 * - StrongBox-backed with TEE fallback
 * - Mandatory biometric authentication for key use
 * - ECDSA signing operations
 *
 * Security Level Priority:
 * 1. StrongBox (dedicated hardware security module)
 * 2. TEE (Trusted Execution Environment)
 * 3. Software (fallback - not recommended)
 */
class RaypherKeyManager {

    companion object {
        private const val KEY_ALIAS = "raypher_pro_hardware_key"
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val TAG = "RaypherKeyManager"
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply { load(null) }

    /**
     * Generate EC key with StrongBox/TEE hardware backing.
     *
     * Returns:
     * - Success: "STRONGBOX" or "TRUSTED_ENVIRONMENT"
     * - Failure: Exception with error details
     */
    fun generateHardwareKey(): Result<String> = runCatching {
        // Attempt StrongBox first
        val strongBoxResult = tryGenerateKey(isStrongBox = true)
        if (strongBoxResult.isSuccess) {
            Log.i(TAG, "✓ StrongBox hardware key generated")
            return@runCatching "STRONGBOX"
        }

        // Fallback to TEE
        Log.w(TAG, "StrongBox unavailable, falling back to TEE")
        tryGenerateKey(isStrongBox = false).getOrThrow()
        Log.i(TAG, "✓ TEE hardware key generated")
        "TRUSTED_ENVIRONMENT"
    }

    /** Internal: Attempt key generation with specified security level. */
    private fun tryGenerateKey(isStrongBox: Boolean): Result<Unit> = runCatching {
        val keyGenSpec =
                KeyGenParameterSpec.Builder(
                                KEY_ALIAS,
                                KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
                        )
                        .apply {
                            // EC curve secp256r1 (NIST P-256)
                            setAlgorithmParameterSpec(
                                    java.security.spec.ECGenParameterSpec("secp256r1")
                            )
                            setDigests(KeyProperties.DIGEST_SHA256)

                            // Hardware security level
                            if (isStrongBox) {
                                setIsStrongBoxBacked(true)
                            }

                            // Biometric authentication required for EVERY key use
                            setUserAuthenticationRequired(true)
                            setUserAuthenticationParameters(
                                    0, // 0 = authentication required per-use (not time-based)
                                    KeyProperties.AUTH_BIOMETRIC_STRONG
                            )

                            // Invalidate key if new biometrics enrolled (security best practice)
                            setInvalidatedByBiometricEnrollment(true)
                        }
                        .build()

        val keyPairGenerator =
                KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, KEYSTORE_PROVIDER)

        keyPairGenerator.initialize(keyGenSpec)
        keyPairGenerator.generateKeyPair()
    }

    /**
     * Sign test data using the hardware-backed biometric key.
     *
     * @param signature Pre-initialized Signature object from BiometricPrompt.CryptoObject
     * @return Signature bytes on success
     */
    fun signWithBiometricKey(signature: Signature): Result<ByteArray> = runCatching {
        val testData = "Raypher Pro Biometric Test".toByteArray()
        signature.update(testData)
        val signatureBytes = signature.sign()

        Log.i(TAG, "✓ Signed data with hardware key (${signatureBytes.size} bytes)")
        signatureBytes
    }

    /** Check if hardware key exists in KeyStore. */
    fun keyExists(): Boolean {
        return keyStore.containsAlias(KEY_ALIAS)
    }

    /** Get the private key for signing (used to initialize Signature object). */
    fun getPrivateKey(): PrivateKey {
        val entry =
                keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.PrivateKeyEntry
                        ?: throw IllegalStateException("Key not found: $KEY_ALIAS")
        return entry.privateKey as PrivateKey
    }
}
