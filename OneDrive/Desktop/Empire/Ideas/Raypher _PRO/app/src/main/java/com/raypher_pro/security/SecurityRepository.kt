package com.Raypher_Pro.security

import kotlinx.coroutines.flow.Flow

/**
 * Domain layer repository interface (Clean Architecture contract).
 *
 * This interface defines the operations needed for security/biometric functionality without
 * exposing Android-specific implementation details.
 *
 * Pure Kotlin - no Android dependencies in the domain layer.
 */
interface SecurityRepository {

    /**
     * Generate a hardware-backed EC key in AndroidKeyStore.
     *
     * Attempts StrongBox-backed key generation, falls back to TEE if unavailable. Key requires
     * biometric authentication for every use.
     *
     * @return Flow emitting SecurityState updates during key generation
     */
    fun generateKey(): Flow<SecurityState>

    /**
     * Trigger biometric authentication and sign test data with the hardware key.
     *
     * @return Flow emitting SecurityState updates during authentication
     */
    fun authenticate(): Flow<SecurityState>

    /** Check if a hardware key already exists in the KeyStore */
    suspend fun hasKey(): Boolean
}
