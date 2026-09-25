package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A crisp, minimalist black & white hot dog emoji graphic that adapts to dark/light themes.
 */
@Composable
fun BlackWhiteHotDogEmoji(
    modifier: Modifier = Modifier,
    sizeDp: Dp = 140.dp
) {
    val isDark = isSystemInDarkTheme()
    val strokeColor = if (isDark) Color(0xFFF4F4F5) else Color(0xFF141416)
    val bunFillColor = if (isDark) Color(0xFF242428) else Color(0xFFFFFFFF)
    val sausageFillColor = if (isDark) Color(0xFF323238) else Color(0xFFF0F0F2)

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(sizeDp)) {
            val w = size.width
            val h = size.height
            val strokeW = w * 0.035f

            rotate(degrees = -18f, pivot = Offset(w / 2f, h / 2f)) {
                // 1. Sausage body (rounded capsule extending past the bun)
                val sausageW = w * 0.88f
                val sausageH = h * 0.32f
                val sausageLeft = (w - sausageW) / 2f
                val sausageTop = (h - sausageH) / 2f

                val sausagePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = sausageLeft,
                            top = sausageTop,
                            right = sausageLeft + sausageW,
                            bottom = sausageTop + sausageH,
                            cornerRadius = CornerRadius(sausageH / 2f, sausageH / 2f)
                        )
                    )
                }

                // Fill sausage
                drawPath(sausagePath, color = sausageFillColor, style = Fill)
                // Outline sausage
                drawPath(
                    sausagePath,
                    color = strokeColor,
                    style = Stroke(
                        width = strokeW,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 2. Bun bottom cradle
                val bunW = w * 0.76f
                val bunH = h * 0.40f
                val bunLeft = (w - bunW) / 2f
                val bunTop = sausageTop - h * 0.04f

                val bunPath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = bunLeft,
                            top = bunTop,
                            right = bunLeft + bunW,
                            bottom = bunTop + bunH,
                            cornerRadius = CornerRadius(bunH * 0.45f, bunH * 0.45f)
                        )
                    )
                }

                // Fill Bun
                drawPath(bunPath, color = bunFillColor, style = Fill)
                // Outline Bun
                drawPath(
                    bunPath,
                    color = strokeColor,
                    style = Stroke(
                        width = strokeW,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )

                // 3. Inner bun crease / opening
                val creasePath = Path().apply {
                    moveTo(bunLeft + bunW * 0.12f, bunTop + bunH * 0.45f)
                    quadraticTo(
                        bunLeft + bunW * 0.5f,
                        bunTop + bunH * 0.55f,
                        bunLeft + bunW * 0.88f,
                        bunTop + bunH * 0.45f
                    )
                }
                drawPath(
                    creasePath,
                    color = strokeColor,
                    style = Stroke(
                        width = strokeW * 0.85f,
                        cap = StrokeCap.Round
                    )
                )

                // 4. Sausage diagonal cuts at ends
                val cutW = strokeW * 0.8f
                // Left end cuts
                drawLine(
                    color = strokeColor,
                    start = Offset(sausageLeft + sausageW * 0.04f, sausageTop + sausageH * 0.3f),
                    end = Offset(sausageLeft + sausageW * 0.08f, sausageTop + sausageH * 0.7f),
                    strokeWidth = cutW,
                    cap = StrokeCap.Round
                )
                // Right end cuts
                drawLine(
                    color = strokeColor,
                    start = Offset(sausageLeft + sausageW * 0.92f, sausageTop + sausageH * 0.3f),
                    end = Offset(sausageLeft + sausageW * 0.96f, sausageTop + sausageH * 0.7f),
                    strokeWidth = cutW,
                    cap = StrokeCap.Round
                )

                // 5. Mustard wavy zigzag line across the sausage
                val mustardPath = Path().apply {
                    val startX = bunLeft + bunW * 0.08f
                    val endX = bunLeft + bunW * 0.92f
                    val waveY = bunTop + bunH * 0.42f
                    val segments = 6
                    val segW = (endX - startX) / segments

                    moveTo(startX, waveY)
                    for (i in 0 until segments) {
                        val segStartX = startX + i * segW
                        val midX = segStartX + segW / 2f
                        val nextX = segStartX + segW
                        val amplitude = if (i % 2 == 0) -h * 0.09f else h * 0.09f
                        quadraticTo(midX, waveY + amplitude, nextX, waveY)
                    }
                }

                drawPath(
                    mustardPath,
                    color = strokeColor,
                    style = Stroke(
                        width = strokeW * 1.3f,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
