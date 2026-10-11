package com.gymrpg.core.designsystem

import android.animation.ValueAnimator
import android.content.Context
import android.os.Build
import android.os.PowerManager
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import com.kyant.backdrop.backdrops.LayerBackdrop
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

/** Draw modes stay internal to the app's surfaces; feature modules never import Backdrop. */
enum class GymGlassMode { FULL, EFFICIENT }
enum class GymColorMode { DARK, LIGHT, SYSTEM }

internal data class GlassContext(
    val mode: GymGlassMode,
    val reducedMotion: Boolean,
    val dark: Boolean,
    val backdrop: LayerBackdrop?
)
internal val LocalGlassContext = staticCompositionLocalOf { GlassContext(GymGlassMode.EFFICIENT, false, true, null) }

val GymNumericTextStyle = SpanStyle(fontFeatureSettings = "tnum")

private val darkColors = darkColorScheme(
    primary = Color(0xFF9CC9FF), onPrimary = Color(0xFF102436),
    background = Color(0xFF09111D), onBackground = Color(0xFFE8F1FC),
    surface = Color(0xFF111D2C), onSurface = Color(0xFFE8F1FC),
    surfaceVariant = Color(0xFF203245), onSurfaceVariant = Color(0xFFBBD0E3)
)
private val lightColors = lightColorScheme(
    primary = Color(0xFF15558C), background = Color(0xFFF0F5FB),
    onBackground = Color(0xFF152536), surface = Color.White,
    onSurface = Color(0xFF152536)
)

// The six axes are present in the official v4.007 font bundled with this module.
// Android's font loader applies variations where supported; the platform font covers
// scripts and symbols absent from Google Sans Flex.
@OptIn(ExperimentalTextApi::class)
private fun gymFont(weight: Int, width: Float, optical: Float, round: Float, grade: Float = 0f, slant: Float = 0f): FontFamily =
    FontFamily(Font(
        R.font.google_sans_flex,
        weight = FontWeight(weight),
        variationSettings = FontVariation.Settings(
            FontVariation.Setting("wght", weight.toFloat()),
            FontVariation.Setting("wdth", width),
            FontVariation.Setting("opsz", optical),
            FontVariation.Setting("ROND", round),
            FontVariation.Setting("GRAD", grade),
            FontVariation.Setting("slnt", slant)
        )
    ))

private fun style(size: Int, line: Int, weight: Int, width: Float, round: Float, system: Boolean): TextStyle =
    TextStyle(
        fontFamily = if (system) FontFamily.Default else gymFont(weight, width, size.toFloat().coerceIn(6f, 144f), round),
        fontWeight = FontWeight(weight),
        fontSize = size.sp,
        lineHeight = line.sp
    )

/** Independent Material 3 type scale; system font fallback is an explicit user preference. */
fun GymTypography(useSystemFont: Boolean = false): Typography = Typography(
    displayLarge = style(57, 64, 760, 122f, 42f, useSystemFont),
    displayMedium = style(45, 52, 740, 118f, 40f, useSystemFont),
    displaySmall = style(36, 44, 710, 114f, 36f, useSystemFont),
    headlineLarge = style(32, 40, 780, 120f, 38f, useSystemFont),
    headlineMedium = style(28, 36, 780, 120f, 38f, useSystemFont),
    headlineSmall = style(24, 32, 700, 114f, 34f, useSystemFont),
    titleLarge = style(22, 30, 640, 110f, 30f, useSystemFont),
    titleMedium = style(16, 24, 630, 108f, 28f, useSystemFont),
    titleSmall = style(14, 20, 630, 106f, 28f, useSystemFont),
    bodyLarge = style(16, 26, 430, 102f, 18f, useSystemFont),
    bodyMedium = style(14, 22, 430, 102f, 18f, useSystemFont),
    bodySmall = style(12, 18, 420, 100f, 16f, useSystemFont),
    labelLarge = style(14, 20, 680, 108f, 46f, useSystemFont),
    labelMedium = style(12, 18, 650, 105f, 38f, useSystemFont),
    labelSmall = style(11, 16, 620, 104f, 35f, useSystemFont)
)

@Composable
fun GymRpgTheme(
    fullGlass: Boolean = false,
    useSystemFont: Boolean = false,
    reducedMotion: Boolean = false,
    colorMode: GymColorMode = GymColorMode.DARK,
    content: @Composable () -> Unit
) {
    val dark = when (colorMode) {
        GymColorMode.DARK -> true
        GymColorMode.LIGHT -> false
        GymColorMode.SYSTEM -> isSystemInDarkTheme()
    }
    val powerManager = LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager
    val fullSupported = Build.VERSION.SDK_INT >= 33 && !reducedMotion && ValueAnimator.areAnimatorsEnabled() && !powerManager.isPowerSaveMode
    val mode = if (fullGlass && fullSupported) GymGlassMode.FULL else GymGlassMode.EFFICIENT
    val backdrop = rememberLayerBackdrop()
    MaterialTheme(colorScheme = if (dark) darkColors else lightColors, typography = GymTypography(useSystemFont)) {
        CompositionLocalProvider(LocalGlassContext provides GlassContext(mode, reducedMotion, dark, backdrop), LocalContentColor provides MaterialTheme.colorScheme.onBackground) {
            Box(Modifier.fillMaxSize().background(if (dark) darkColors.background else lightColors.background)) {
                // Capture a stable background layer. Capturing the whole UI here makes
                // a glass surface sample itself and yields feedback artifacts.
                Box(
                    Modifier.fillMaxSize().layerBackdrop(backdrop).background(
                        Brush.linearGradient(
                            if (dark) listOf(Color(0xFF0B1727), Color(0xFF172E43), Color(0xFF071222))
                            else listOf(Color(0xFFF4F9FD), Color(0xFFDDEBF8), Color(0xFFECF5FF))
                        )
                    )
                )
                content()
            }
        }
    }
}
