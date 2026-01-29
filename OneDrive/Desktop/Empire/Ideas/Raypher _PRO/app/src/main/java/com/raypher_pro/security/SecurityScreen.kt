package com.Raypher_Pro.security

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Main security screen UI using Jetpack Compose + Material 3.
 *
 * Visual States:
 * - Locked: Red shield with breathing animation
 * - Success: Green shield with pulsing animation + "Silicon Secured" badge
 * - Loading: Circular progress indicator
 * - Error: Shows error message with red accent
 *
 * Design: Military-grade aesthetic with dark theme optimization.
 */
@Composable
fun SecurityScreen(
        state: SecurityState,
        onAuthenticateClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        // Dark background for security context
        Surface(
                modifier = modifier.fillMaxSize(),
                color = Color(0xFF0F172A) // Deep Charcoal
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                ) {
                        // Top: Status text
                        StatusHeader(state)

                        // Center: Shield icon
                        ShieldIcon(state)

                        // Bottom: Action button
                        ActionButton(state, onAuthenticateClick)
                }
        }
}

/** Status header showing current security state. */
@Composable
private fun StatusHeader(state: SecurityState) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
        ) {
                // Circuit "R" Monogram Logo
                Image(
                        painter = painterResource(id = R.drawable.raypher_logo),
                        contentDescription = "Raypher Circuit Logo",
                        modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                        text = "RAYPHER",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF64748B), // Slate gray
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (state) {
                        is SecurityState.Success -> {
                                Text(
                                        text = "🔐 SECURE ENCLAVE: ACTIVE",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color(0xFF00D9FF), // Electric Cyan
                                        fontWeight = FontWeight.SemiBold
                                )
                        }
                        is SecurityState.Locked -> {
                                Text(
                                        text = "Authentication Required",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFEF4444) // Red
                                )
                        }
                        is SecurityState.Error -> {
                                Text(
                                        text = state.message,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFFEF4444), // Red
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                )
                        }
                        else -> {}
                }
        }
}

/** Animated shield icon representing security state. */
@Composable
private fun ShieldIcon(state: SecurityState) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                when (state) {
                        is SecurityState.Loading -> {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(120.dp),
                                        color = Color(0xFF00D9FF), // Electric Cyan
                                        strokeWidth = 4.dp
                                )
                        }
                        is SecurityState.Locked, is SecurityState.Error -> {
                                // Red shield with breathing animation
                                BreathingShield(color = Color(0xFFEF4444))
                        }
                        is SecurityState.Success -> {
                                // Green shield with pulsing animation
                                PulsingShield(color = Color(0xFF10B981))
                        }
                        is SecurityState.Idle -> {
                                // Gray shield (neutral state)
                                Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Security Shield",
                                        modifier = Modifier.size(200.dp),
                                        tint = Color(0xFF64748B)
                                )
                        }
                }
        }
}

/** Breathing animation for locked state (subtle scale oscillation). */
@Composable
private fun BreathingShield(color: Color) {
        val infiniteTransition = rememberInfiniteTransition(label = "breathing")
        val scale by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(2000, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "scale"
                )

        Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Locked Shield",
                modifier = Modifier.size(200.dp).scale(scale),
                tint = color
        )
}

/** Pulsing animation for success state (rapid pulse). */
@Composable
private fun PulsingShield(color: Color) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulsing")
        val scale by
                infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.15f,
                        animationSpec =
                                infiniteRepeatable(
                                        animation = tween(600, easing = FastOutSlowInEasing),
                                        repeatMode = RepeatMode.Reverse
                                ),
                        label = "scale"
                )

        Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Authenticated Shield",
                modifier = Modifier.size(200.dp).scale(scale),
                tint = color
        )
}

/** Action button (Verify Identity). */
@Composable
private fun ActionButton(state: SecurityState, onAuthenticateClick: () -> Unit) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 48.dp)
        ) {
                Button(
                        onClick = onAuthenticateClick,
                        enabled = state is SecurityState.Locked || state is SecurityState.Error,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF00D9FF), // Electric Cyan
                                        contentColor = Color(0xFF0A0E1A), // Dark text on cyan
                                        disabledContainerColor = Color(0xFF1E293B) // Dark gray
                                )
                ) {
                        Text(
                                text =
                                        when (state) {
                                                is SecurityState.Loading -> "AUTHENTICATING..."
                                                is SecurityState.Success -> "VERIFIED ✓"
                                                else -> "VERIFY IDENTITY"
                                        },
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                        )
                }

                // Hint text
                if (state is SecurityState.Locked || state is SecurityState.Error) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = "Hardware-backed biometric authentication",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                        )
                }
        }
}
