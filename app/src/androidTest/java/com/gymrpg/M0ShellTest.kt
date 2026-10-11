package com.gymrpg

import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.graphics.asAndroidBitmap
import android.graphics.Bitmap
import android.view.WindowInsets
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.pinch
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlin.math.min
import java.io.FileOutputStream
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** M0 proof only. Tags are the shell's accessible interaction contract, not a layout hierarchy. */
@RunWith(AndroidJUnit4::class)
class M0ShellTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun fiveDestinationsOpenFromFloatingNavigation() {
        listOf("Home", "Workout", "Calendar", "Food", "Profile").forEach { destination ->
            rule.onNodeWithTag("tab_$destination").performClick()
            rule.onNodeWithTag("screen_title").assertTextContains(destination)
        }
    }

    @Test fun glassModeCanSwitchBothWays() {
        setFullGlass(true)
        rule.onNodeWithTag("glass_full").assertIsDisplayed()
        setFullGlass(false)
        rule.onNodeWithTag("glass_efficient").assertIsDisplayed()
        rule.onNodeWithTag("pill_nav").assertIsDisplayed()
    }

    @Test fun anatomyViewSwitchesAndSelectingRegionReportsItsMapping() {
        rule.onNodeWithTag("tab_Home").performClick()
        rule.onNodeWithTag("anatomy_front").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_front").performScrollTo().performTouchInput {
            // Tap the center of the first mapped front path (Right Neck) using the
            // canvas' 35 x 93 SVG viewBox transform, independent of screen density.
            val fit = min(width / 35f, height / 93f)
            val bodyWidth = 35f * fit
            val x = (width - bodyWidth) / 2f + 14.2f * fit
            val y = (height - 93f * fit) / 2f + 14.2f * fit
            click(Offset(x, y))
        }
        rule.onNodeWithTag("anatomy_selection").performScrollTo().assertTextEquals("Selected: Right Neck • sternocleidomastoid • approximation")
        rule.onNodeWithTag("anatomy_back").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_back").performScrollTo().assertIsDisplayed()
        rule.onNodeWithTag("anatomy_selection").assertDoesNotExist()
    }

    @Test fun anatomyZoomCanBeReset() {
        rule.onNodeWithTag("tab_Home").performClick()
        rule.onNodeWithTag("anatomy_front").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_reset").assertIsNotEnabled()
        rule.onNodeWithTag("anatomy_canvas_front").performScrollTo().performTouchInput {
            pinch(Offset(center.x - 35f, center.y), Offset(center.x - 90f, center.y), Offset(center.x + 35f, center.y), Offset(center.x + 90f, center.y))
        }
        rule.onNodeWithTag("anatomy_reset").assertIsEnabled()
        rule.onNodeWithTag("anatomy_reset").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_reset").assertIsNotEnabled()
    }

    @Test fun bottomNavigationStaysAboveGestureArea() {
        rule.waitForIdle()
        val bounds = rule.onNodeWithTag("pill_nav").fetchSemanticsNode().boundsInWindow
        val decor = rule.activity.window.decorView
        val navBottom = decor.rootWindowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
        assertTrue("Navigation needs a visible 48dp touch target: $bounds", bounds.height >= decor.resources.displayMetrics.density * 48)
        assertTrue("Pill obscured by navigation inset ($navBottom px): $bounds", bounds.bottom <= decor.height - navBottom + 2f)
        Log.i("M0Insets", "api=${android.os.Build.VERSION.SDK_INT} decor=${decor.width}x${decor.height} pill=$bounds navBottom=$navBottom")
        val statusTop = decor.rootWindowInsets.getInsets(WindowInsets.Type.statusBars()).top
        val header = rule.onNodeWithTag("m0_header").fetchSemanticsNode().boundsInWindow
        assertTrue("Header obscured by status inset ($statusTop px): $header", header.top >= statusTop - 2f)
        Log.i("M0Insets", "header=$header statusTop=$statusTop cutout=${decor.rootWindowInsets.getInsets(WindowInsets.Type.displayCutout()).top}")
    }

    @Test fun keyboardDoesNotCoverFocusedField() {
        rule.onNodeWithTag("tab_Profile").performClick()
        rule.onNodeWithTag("ime_probe").performScrollTo().performClick()
        rule.onNodeWithTag("ime_probe").performTextInput("M0 inset check")
        rule.waitForIdle()
        val decor = rule.activity.window.decorView
        rule.waitUntil(timeoutMillis = 10_000) {
            decor.rootWindowInsets.getInsets(WindowInsets.Type.ime()).bottom > 0
        }
        val ime = decor.rootWindowInsets.getInsets(WindowInsets.Type.ime()).bottom
        assertTrue("IME did not open; device keyboard settings need inspection", ime > 0)
        val field = rule.onNodeWithTag("ime_probe").fetchSemanticsNode().boundsInWindow
        assertTrue("Focused field covered by IME: field=$field ime=$ime", field.bottom <= decor.height - ime + 2f)
        Log.i("M0Insets", "ime=$ime field=$field decorHeight=${decor.height}")
    }

    @Test fun captureFullGlassHomeAndBothAnatomyViews() {
        setFullGlass(true)
        rule.onNodeWithTag("tab_Home").performClick()
        rule.onNodeWithTag("anatomy_front").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_front").performScrollTo()
        capture("full-glass-front.png")
        rule.onNodeWithTag("anatomy_back").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_back").performScrollTo()
        capture("full-glass-back.png")
    }

    @Test fun captureEfficientGlassHomeAndBothAnatomyViews() {
        setFullGlass(false)
        rule.onNodeWithTag("tab_Home").performClick()
        rule.onNodeWithTag("anatomy_front").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_front").performScrollTo()
        capture("efficient-glass-front.png")
        rule.onNodeWithTag("anatomy_back").performScrollTo().performClick()
        rule.onNodeWithTag("anatomy_canvas_back").performScrollTo()
        capture("efficient-glass-back.png")
    }

    private fun setFullGlass(enabled: Boolean) {
        val chip = rule.onNodeWithTag("glass_toggle")
        val selected = chip.fetchSemanticsNode().config.getOrNull(SemanticsProperties.Selected) == true
        if (selected != enabled) chip.performClick()
        // DataStore commits on IO; Compose idleness alone does not await that write.
        val expected = if (enabled) "glass_full" else "glass_efficient"
        rule.waitUntil(timeoutMillis = 10_000) {
            rule.onAllNodesWithTag(expected).fetchSemanticsNodes().size == 1
        }
        rule.onNodeWithTag(expected).assertIsDisplayed()
    }

    private fun capture(name: String) {
        rule.waitForIdle()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dir = File(context.getExternalFilesDir(null), "m0-screenshots").apply { mkdirs() }
        FileOutputStream(File(dir, name)).use { out ->
            val bitmap = rule.onRoot().captureToImage().asAndroidBitmap()
            assertTrue("Could not encode $name", bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
        }
    }
}
