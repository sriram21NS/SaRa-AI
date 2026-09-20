package com.example.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SakuraPetalPink
import com.example.ui.theme.SakuraPrimaryLight
import kotlin.math.sin

enum class SaraAnimationState {
    IDLE,
    GREETING,
    THINKING,
    SPEAKING,
    HAPPY,
    GUIDING,
    SURPRISED,
    GOODBYE
}

enum class SaraSize {
    HERO,
    MEDIUM,
    AVATAR
}

@Composable
fun SaraAnimatedCharacter(
    modifier: Modifier = Modifier,
    state: SaraAnimationState = SaraAnimationState.IDLE,
    size: SaraSize = SaraSize.HERO,
    isSpeaking: Boolean = false,
    dialogueBubbleText: String? = null,
    onCharacterTap: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sara_char_anim")

    // Breathing / Gentle bobbing oscillation
    val breathProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath_anim"
    )

    // Voice pulsing aura when speaking
    val voicePulse by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "voice_pulse"
    )

    // Thinking sparkle rotation
    val sparkleRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sparkle_rotate"
    )

    val currentDrawable = when (state) {
        SaraAnimationState.GREETING, SaraAnimationState.GOODBYE -> R.drawable.img_sara_greeting
        SaraAnimationState.THINKING, SaraAnimationState.SURPRISED -> R.drawable.img_sara_thinking
        SaraAnimationState.SPEAKING, SaraAnimationState.GUIDING -> R.drawable.img_sara_speaking
        SaraAnimationState.HAPPY -> R.drawable.img_sara_greeting
        SaraAnimationState.IDLE -> R.drawable.img_sara_idle
    }

    val (frameWidth, frameHeight, cornerRadius) = when (size) {
        SaraSize.HERO -> Triple(220.dp, 280.dp, 28.dp)
        SaraSize.MEDIUM -> Triple(130.dp, 165.dp, 20.dp)
        SaraSize.AVATAR -> Triple(48.dp, 48.dp, 24.dp)
    }

    val bobbingY = if (size != SaraSize.AVATAR) (sin(breathProgress * Math.PI) * 4).dp else 0.dp
    val breathScale = if (size != SaraSize.AVATAR) 1.0f + (breathProgress * 0.02f) else 1.0f

    var showTapQuote by remember { mutableStateOf(false) }
    val tapQuotes = remember {
        listOf(
            "Hi there! 🌸 What shall we explore today?",
            "I'm right here beside you! ✨",
            "You're doing great! Keep going! 💡",
            "Let's solve this step by step! 😊",
            "Coding, learning, or creating? I'm ready! 📚"
        )
    }
    var currentTapQuote by remember { mutableStateOf(tapQuotes.first()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.testTag("sara_character_container")
    ) {
        // Dialogue / State Bubble
        val activeText = dialogueBubbleText ?: if (showTapQuote) currentTapQuote else when (state) {
            SaraAnimationState.THINKING -> "Thinking carefully... 💡"
            SaraAnimationState.SPEAKING -> "Guiding you through this 🌸"
            SaraAnimationState.GREETING -> "Welcome! Lovely to see you! ✨"
            SaraAnimationState.GUIDING -> "Follow these steps with me 🧭"
            SaraAnimationState.HAPPY -> "Wonderful progress! 🎉"
            else -> null
        }

        if (activeText != null && size != SaraSize.AVATAR) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                shadowElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.linearGradient(listOf(SakuraPrimaryLight.copy(alpha = 0.6f), SakuraPetalPink))
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = activeText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }

        // Animated Character Frame
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset(y = bobbingY)
                .scale(breathScale)
                .clickable(enabled = onCharacterTap != null || size != SaraSize.AVATAR) {
                    currentTapQuote = tapQuotes.random()
                    showTapQuote = true
                    onCharacterTap?.invoke()
                }
        ) {
            // Speaking Aura
            if (isSpeaking || state == SaraAnimationState.SPEAKING) {
                Box(
                    modifier = Modifier
                        .size(frameWidth + 18.dp, frameHeight + 18.dp)
                        .scale(voicePulse)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SakuraPrimaryLight.copy(alpha = 0.35f),
                                    SakuraPetalPink.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            shape = RoundedCornerShape(cornerRadius + 4.dp)
                        )
                )
            }

            // Character Card Canvas
            Box(
                modifier = Modifier
                    .size(frameWidth, frameHeight)
                    .shadow(
                        elevation = if (size == SaraSize.HERO) 12.dp else 4.dp,
                        shape = RoundedCornerShape(cornerRadius),
                        ambientColor = SakuraPrimaryLight.copy(alpha = 0.2f),
                        spotColor = SakuraPrimaryLight.copy(alpha = 0.4f)
                    )
                    .clip(RoundedCornerShape(cornerRadius))
                    .border(
                        width = if (size == SaraSize.AVATAR) 1.5.dp else 2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                SakuraPetalPink.copy(alpha = 0.8f),
                                SakuraPrimaryLight.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Crossfade(
                    targetState = currentDrawable,
                    animationSpec = tween(400),
                    label = "sara_image_crossfade"
                ) { drawableRes ->
                    Image(
                        painter = painterResource(id = drawableRes),
                        contentDescription = "SaRa Anime Companion - $state",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Interactive indicator badge
                if (size == SaraSize.HERO) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, SakuraPetalPink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .background(
                                        if (isSpeaking) SakuraPrimaryLight else Color(0xFF10B981),
                                        CircleShape
                                    )
                            )
                            Text(
                                text = if (isSpeaking) "Speaking" else state.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
