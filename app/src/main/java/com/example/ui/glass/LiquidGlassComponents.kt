package com.example.ui.glass

import android.app.Activity
import android.graphics.Rect
import android.view.ViewGroup
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ThemeManager

/**
 * Optical styles for Liquid Glass
 */
enum class GlassIntensity {
    LIGHT,       // Subtle crystal refraction for option chips and lists
    STANDARD,    // Balanced liquid glass for primary cards
    HERO,        // Deep optical refraction, dispersion & caustic glow for header banners & stats
    ALERT_ERROR, // Tempered crimson glass for wrong answers
    ALERT_CORRECT// Quenched emerald glass for correct answers
}

/**
 * Liquid Glass Card with physical refraction, chromatic dispersion border,
 * specular highlight reflection, and caustic ambient lighting.
 * Completely avoids opaque elevation shadow blocks to eliminate black rectangle artifacts on all devices.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 22.dp,
    intensity: GlassIntensity = GlassIntensity.STANDARD,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }
    val isDark = ThemeManager.isDarkMode

    // Dynamic tint and refraction styling adapted for Dark/Light mode
    val (glassBackground, rimBrush, specularSheen) = when (intensity) {
        GlassIntensity.LIGHT -> {
            if (isDark) {
                Triple(
                    Color(0x301E293B),
                    Brush.linearGradient(
                        listOf(
                            Color(0x8094A3B8),
                            Color(0x2538BDF8),
                            Color(0x10FFFFFF),
                            Color(0x4038BDF8)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    Color(0xCCFFFFFF)
                )
            } else {
                Triple(
                    Color(0xEEFFFFFF),
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFE2E8F0),
                            Color(0x8038BDF8),
                            Color(0x40E2E8F0),
                            Color(0x80BAE6FD)
                        )
                    ),
                    Color(0xAAFFFFFF)
                )
            }
        }
        GlassIntensity.STANDARD -> {
            if (isDark) {
                Triple(
                    Color(0x48151E30),
                    Brush.linearGradient(
                        listOf(
                            Color(0xB394A3B8),
                            Color(0x4538BDF8),
                            Color(0x20FFFFFF),
                            Color(0x60F97316)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    Color(0xEEFFFFFF)
                )
            } else {
                Triple(
                    Color(0xF5FFFFFF),
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFCBD5E1),
                            Color(0x800284C7),
                            Color(0x40CBD5E1),
                            Color(0x60F97316)
                        )
                    ),
                    Color(0xCCFFFFFF)
                )
            }
        }
        GlassIntensity.HERO -> {
            if (isDark) {
                Triple(
                    Color(0x600F172A),
                    Brush.linearGradient(
                        listOf(
                            Color(0xEEFFFFFF),
                            Color(0x7038BDF8),
                            Color(0x30FFFFFF),
                            Color(0x80F97316)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    ),
                    Color(0xFFFFFFFF)
                )
            } else {
                Triple(
                    Color(0xFAFFFFFF),
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0284C7),
                            Color(0x9038BDF8),
                            Color(0x40F97316),
                            Color(0x900284C7)
                        )
                    ),
                    Color(0xEEFFFFFF)
                )
            }
        }
        GlassIntensity.ALERT_ERROR -> {
            Triple(
                if (isDark) Color(0x40EF4444) else Color(0x20EF4444),
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFCA5A5),
                        Color(0x80EF4444),
                        Color(0x40B91C1C),
                        Color(0x90EF4444)
                    )
                ),
                Color(0xEEFFFFFF)
            )
        }
        GlassIntensity.ALERT_CORRECT -> {
            Triple(
                if (isDark) Color(0x4010B981) else Color(0x2010B981),
                Brush.linearGradient(
                    listOf(
                        Color(0xFF6EE7B7),
                        Color(0x8010B981),
                        Color(0x40047857),
                        Color(0x9010B981)
                    )
                ),
                Color(0xEEFFFFFF)
            )
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassBackground)
            .border(
                width = 1.2.dp,
                brush = rimBrush,
                shape = shape
            )
            // Optical dispersion & specular top-edge sheen
            .drawWithContent {
                drawContent()
                // Top-left specular gloss line (light entering glass)
                val cPx = cornerRadius.toPx()
                if (size.width > cPx * 2) {
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                specularSheen.copy(alpha = 0.4f),
                                specularSheen.copy(alpha = 0.9f),
                                specularSheen.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        start = Offset(cPx, 1.5f),
                        end = Offset(size.width - cPx, 1.5f),
                        strokeWidth = 2f
                    )
                }
            }
    ) {
        content()
    }
}

/**
 * Background placed directly underneath all UI buttons and cards ("位于按钮排版的下方").
 * - If a custom image is chosen by the user from phone files: draws the custom image with a subtle glass scrim.
 * - If default: draws the animated furnace thermal embers in Dark mode, or luminous pearl sky gradient in Light mode.
 */
@Composable
fun ThermalFurnaceBackground(
    modifier: Modifier = Modifier
) {
    val isDark = ThemeManager.isDarkMode
    val customBitmap = ThemeManager.customBackgroundBitmap
    val hasCustom = ThemeManager.hasCustomBackground && customBitmap != null

    val infiniteTransition = rememberInfiniteTransition(label = "furnace_flow")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val offsetFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetFlow"
    )

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        if (hasCustom && customBitmap != null) {
            // Render user's custom photo from their phone storage
            val bmp = customBitmap.asAndroidBitmap()
            val bmpW = bmp.width
            val bmpH = bmp.height

            // Calculate center-crop destination rect
            val scale = maxOf(w / bmpW.toFloat(), h / bmpH.toFloat())
            val scaledW = bmpW * scale
            val scaledH = bmpH * scale
            val left = (w - scaledW) / 2f
            val top = (h - scaledH) / 2f

            drawImage(
                image = customBitmap,
                dstOffset = IntOffset(left.toInt(), top.toInt()),
                dstSize = IntSize(scaledW.toInt(), scaledH.toInt())
            )

            // Frosted glass veil scrim to ensure button text on top remains crystal clear
            drawRect(
                color = if (isDark) Color(0xAA090D16) else Color(0x88F8FAFC)
            )
        } else {
            // Default built-in dynamic theme background
            if (isDark) {
                // Tempered dark steel furnace
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF090D16),
                            Color(0xFF0F172A),
                            Color(0xFF131A2A),
                            Color(0xFF080C14)
                        )
                    )
                )

                // Upper right: Cyan/Blue tempering quench light orb (soft alpha, no clipping)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x350284C7),
                            Color(0x150369A1),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f, h * 0.12f),
                        radius = (w * 0.65f) * pulse1
                    ),
                    center = Offset(w * 0.85f, h * 0.12f),
                    radius = (w * 0.65f) * pulse1
                )

                // Mid-left: Glowing molten steel furnace ember
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x38EA580C),
                            Color(0x18F97316),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f, h * (0.45f + offsetFlow * 0.1f)),
                        radius = (w * 0.7f) * pulse1
                    ),
                    center = Offset(w * 0.15f, h * (0.45f + offsetFlow * 0.1f)),
                    radius = (w * 0.7f) * pulse1
                )

                // Bottom right: Deep thermal violet induction field
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x226366F1),
                            Color(0x0C4F46E5),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.75f, h * 0.85f),
                        radius = w * 0.6f
                    ),
                    center = Offset(w * 0.75f, h * 0.85f),
                    radius = w * 0.6f
                )
            } else {
                // Light mode: Pure crystal pearl sky gradient
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFE2E8F0),
                            Color(0xFFF1F5F9),
                            Color(0xFFE0F2FE),
                            Color(0xFFF8FAFC)
                        )
                    )
                )

                // Light sky azure glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x4038BDF8),
                            Color(0x150284C7),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.85f, h * 0.12f),
                        radius = (w * 0.65f) * pulse1
                    ),
                    center = Offset(w * 0.85f, h * 0.12f),
                    radius = (w * 0.65f) * pulse1
                )

                // Warm sun amber reflection
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x30FDBA74),
                            Color(0x10F97316),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.15f, h * (0.45f + offsetFlow * 0.1f)),
                        radius = (w * 0.7f) * pulse1
                    ),
                    center = Offset(w * 0.15f, h * (0.45f + offsetFlow * 0.1f)),
                    radius = (w * 0.7f) * pulse1
                )
            }
        }
    }
}
