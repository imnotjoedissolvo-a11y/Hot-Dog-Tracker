package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val id: Int,
    val initialVx: Float,
    val initialVy: Float,
    val color: Color,
    val width: Float,
    val height: Float,
    val isCircle: Boolean,
    val rotationSpeed: Float,
    val initialRotation: Float
)

@Composable
fun ConfettiExplosion(
    triggerKey: Int,
    modifier: Modifier = Modifier
) {
    if (triggerKey == 0) return

    val progress = remember(triggerKey) { Animatable(0f) }

    val particles = remember(triggerKey) {
        val colors = listOf(
            Color(0xFFFF3B30), // Red
            Color(0xFFFFCC00), // Mustard / Gold
            Color(0xFF34C759), // Green
            Color(0xFF007AFF), // Blue
            Color(0xFFAF52DE), // Purple
            Color(0xFFFF9500), // Orange
            Color(0xFFFF2D55), // Hot pink
            Color(0xFF5856D6)  // Indigo
        )
        val rnd = Random(triggerKey.toLong())
        List(48) { index ->
            // Explosion outward in 360 degrees with upward bias
            val angle = (rnd.nextDouble() * 2 * Math.PI).toFloat()
            val speed = rnd.nextFloat() * 450f + 250f
            val vx = (cos(angle) * speed).toFloat()
            val vy = (sin(angle) * speed - 200f).toFloat() // slight upward boost

            Particle(
                id = index,
                initialVx = vx,
                initialVy = vy,
                color = colors[index % colors.size],
                width = rnd.nextFloat() * 12f + 8f,
                height = rnd.nextFloat() * 16f + 8f,
                isCircle = rnd.nextBoolean(),
                rotationSpeed = (rnd.nextFloat() - 0.5f) * 720f,
                initialRotation = rnd.nextFloat() * 360f
            )
        }
    }

    LaunchedEffect(triggerKey) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
        )
    }

    if (progress.value < 1f) {
        val t = progress.value
        val alpha = if (t > 0.65f) (1f - (t - 0.65f) / 0.35f).coerceIn(0f, 1f) else 1f

        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2f
            val centerY = size.height / 2f
            val gravity = 900f // px/s^2

            particles.forEach { p ->
                val seconds = t * 2.2f
                val drag = 1f - (0.35f * t)
                val currentX = centerX + p.initialVx * seconds * drag
                val currentY = centerY + (p.initialVy * seconds * drag) + (0.5f * gravity * seconds * seconds)
                val currentRotation = p.initialRotation + p.rotationSpeed * seconds

                rotate(degrees = currentRotation, pivot = Offset(currentX, currentY)) {
                    if (p.isCircle) {
                        drawCircle(
                            color = p.color.copy(alpha = alpha),
                            radius = p.width / 2f,
                            center = Offset(currentX, currentY)
                        )
                    } else {
                        drawRect(
                            color = p.color.copy(alpha = alpha),
                            topLeft = Offset(currentX - p.width / 2f, currentY - p.height / 2f),
                            size = Size(p.width, p.height)
                        )
                    }
                }
            }
        }
    }
}
