package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Petal(
    val initialX: Float,
    val initialY: Float,
    val speed: Float,
    val size: Float,
    val swayAmplitude: Float,
    val swayPeriod: Float,
    val alpha: Float,
    val rotationSpeed: Float
)

@Composable
fun SakuraParticlesBackground(
    modifier: Modifier = Modifier,
    particleCount: Int = 18,
    tint: Color = Color(0xFFFFB7C5)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sakura_anim")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sakura_progress"
    )

    val petals = remember {
        val rnd = Random(42)
        List(particleCount) {
            Petal(
                initialX = rnd.nextFloat(),
                initialY = rnd.nextFloat(),
                speed = 0.4f + rnd.nextFloat() * 0.6f,
                size = 12f + rnd.nextFloat() * 16f,
                swayAmplitude = 20f + rnd.nextFloat() * 30f,
                swayPeriod = 1.5f + rnd.nextFloat() * 2f,
                alpha = 0.25f + rnd.nextFloat() * 0.35f,
                rotationSpeed = 30f + rnd.nextFloat() * 90f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        petals.forEach { petal ->
            val totalTravel = (petal.initialY + progress * petal.speed) % 1.2f - 0.1f
            val currentY = totalTravel * h
            val sway = sin((progress * petal.swayPeriod * 2 * PI).toFloat()) * petal.swayAmplitude
            val currentX = (petal.initialX * w + sway) % w

            val path = Path().apply {
                val s = petal.size
                moveTo(currentX, currentY - s)
                cubicTo(
                    currentX + s * 0.8f, currentY - s * 0.5f,
                    currentX + s * 0.8f, currentY + s * 0.5f,
                    currentX, currentY + s
                )
                cubicTo(
                    currentX - s * 0.8f, currentY + s * 0.5f,
                    currentX - s * 0.8f, currentY - s * 0.5f,
                    currentX, currentY - s
                )
                close()
            }

            drawPath(
                path = path,
                color = tint.copy(alpha = petal.alpha)
            )
        }
    }
}
