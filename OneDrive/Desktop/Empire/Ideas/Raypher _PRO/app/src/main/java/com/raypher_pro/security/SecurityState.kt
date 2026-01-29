package com.Raypher_Pro.security

/**
 * Domain layer entity representing the current security state of the application.
 *
 * This sealed interface follows Clean Architecture principles by residing in the domain layer (pure
 * Kotlin, no Android dependencies).
 *
 * States:
 * - Idle: Initial state before any user interaction
 * - Loading: Biometric authentication is in progress
 * - Locked: User needs to authenticate (key exists but not yet verified)
 * - Success: Authentication successful + hardware key verified
 * - Error: Authentication failed or key generation error
 */
sealed interface SecurityState {

    /** Initial state - no user action yet */
    data object Idle : SecurityState

    /** Biometric authentication in progress */
    data object Loading : SecurityState

    /** User needs to authenticate with biometrics */
    data object Locked : SecurityState

    /** Authentication successful - hardware key verified */
    data class Success(val message: String) : SecurityState

    /** Error occurred during authentication or key generation */
    data class Error(val message: String) : SecurityState
}
