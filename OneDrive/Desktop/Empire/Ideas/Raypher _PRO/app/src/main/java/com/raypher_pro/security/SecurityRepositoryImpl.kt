package com.Raypher_Pro.security

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Data layer implementation of SecurityRepository (Clean Architecture).
 *
 * Responsibilities:
 * - Orchestrate RaypherKeyManager (hardware key operations)
 * - Orchestrate BiometricPromptManager (biometric UI/authentication)
 * - Map Android-specific results to domain SecurityState
 *
 * Android-specific implementation (requires FragmentActivity for BiometricPrompt).
 */
class SecurityRepositoryImpl(private val activity: FragmentActivity) : SecurityRepository {

    private val keyManager = RaypherKeyManager()
    private val biometricManager = BiometricPromptManager(activity)

    /** Generate hardware-backed EC key (StrongBox/TEE fallback). */
    override fun generateKey(): Flow<SecurityState> = flow {
        emit(SecurityState.Loading)

        val result = keyManager.generateHardwareKey()

        result.fold(
                onSuccess = { securityLevel ->
                    emit(SecurityState.Success("Key created: $securityLevel"))
                },
                onFailure = { error ->
                    emit(SecurityState.Error("Key generation failed: ${error.message}"))
                }
        )
    }

    /** Trigger biometric authentication + sign test data. */
    override fun authenticate(): Flow<SecurityState> =
            flow {
                emit(SecurityState.Loading)

                // Collect biometric prompt results
                biometricManager.authenticate().collect { biometricResult ->
                    biometricResult.fold(
                            onSuccess = { cryptoObject ->
                                // Biometric auth successful - now sign test data
                                val signResult =
                                        keyManager.signWithBiometricKey(cryptoObject.signature!!)

                                signResult.fold(
                                        onSuccess = { signature ->
                                            emit(
                                                    SecurityState.Success(
                                                            "✓ Authenticated with ${signature.size}-byte signature"
                                                    )
                                            )
                                        },
                                        onFailure = { error ->
                                            emit(
                                                    SecurityState.Error(
                                                            "Signing failed: ${error.message}"
                                                    )
                                            )
                                        }
                                )
                            },
                            onFailure = { error ->
                                emit(SecurityState.Error("Biometric failed: ${error.message}"))
                            }
                    )
                }
            }
                    .onStart {
                        // Ensure key exists before authentication
                        if (!hasKey()) {
                            generateKey().collect { emit(it) }
                        }
                    }

    /** Check if hardware key exists in AndroidKeyStore. */
    override suspend fun hasKey(): Boolean {
        return keyManager.keyExists()
    }
}
