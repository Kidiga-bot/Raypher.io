package com.Raypher_Pro.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the security screen (Presentation Layer - MVVM pattern).
 *
 * Responsibilities:
 * - Manage UI state via StateFlow
 * - Coordinate key generation and authentication flows
 * - Map domain SecurityState to UI-friendly messages
 *
 * Android-specific (uses ViewModel, Coroutines), but delegates security operations to the domain
 * layer repository.
 */
class SecurityViewModel(private val repository: SecurityRepository) : ViewModel() {

    private val _state = MutableStateFlow<SecurityState>(SecurityState.Idle)
    val state: StateFlow<SecurityState> = _state.asStateFlow()

    init {
        // Initialize: Check if key exists and set initial state
        viewModelScope.launch {
            val keyExists = repository.hasKey()
            _state.value =
                    if (keyExists) {
                        SecurityState.Locked
                    } else {
                        // No key exists - generate one on first launch
                        repository.generateKey().collect { newState -> _state.value = newState }
                        SecurityState.Locked
                    }
        }
    }

    /** Triggered when user clicks "Verify Identity" button */
    fun onAuthenticateClicked() {
        viewModelScope.launch {
            repository.authenticate().collect { newState ->
                _state.value =
                        when (newState) {
                            is SecurityState.Success ->
                                    SecurityState.Success(
                                            "✓ Authenticated with StrongBox/TEE hardware key"
                                    )
                            is SecurityState.Error -> SecurityState.Error(newState.message)
                            else -> newState
                        }
            }
        }
    }
}
