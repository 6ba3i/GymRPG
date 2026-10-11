package com.gymrpg.feature.anatomy

import org.junit.Assert.assertEquals
import org.junit.Test

class AnatomyCoordinatesTest {
    @Test fun centeredFrontAndBackCoordinates() {
        val front = canvasToSvg(175f, 465f, 350f, 930f, 1f, 0f, 0f, false)
        val back = canvasToSvg(175f, 465f, 350f, 930f, 1f, 0f, 0f, true)
        assertEquals(17.5f, front.first, .001f)
        assertEquals(54.5f, back.first, .001f)
        assertEquals(46.5f, back.second, .001f)
    }

    @Test fun focusCentersVisibleChestWhenWithinPanBounds() {
        val zoom = 2.5f
        val (panX, panY) = panForSvgFocus(17.5f, 20f, 210f, 420f, zoom, false)
        val focused = canvasToSvg(105f, 210f, 210f, 420f, zoom, panX, panY, false)
        assertEquals(17.5f, focused.first, .001f)
        assertEquals(20f, focused.second, .001f)
    }

    @Test fun focusPanRemainsBoundedForSmallBackRegion() {
        val zoom = 2.5f
        val (panX, panY) = panForSvgFocus(70f, 5f, 210f, 420f, zoom, true)
        val (limitX, limitY) = panLimits(210f, 420f, zoom)
        assertEquals(-limitX, panX, .001f)
        assertEquals(limitY, panY, .001f)
    }

    @Test fun inverseZoomAndPanPreservesTargetAtNonSquareViewport() {
        // Back SVG x=55 y=46.5; canvas fit 400/93 with 2x zoom around the centered body.
        val fit = 400f / 93f
        val bodyWidth = 35f * fit
        val x = (420f - bodyWidth) / 2f + 15f - bodyWidth / 2f + (55f - 37f) * fit * 2f
        val y = -10f - 200f + 46.5f * fit * 2f
        val source = canvasToSvg(x, y, 420f, 400f, 2f, 15f, -10f, true)
        assertEquals(55f, source.first, .001f)
        assertEquals(46.5f, source.second, .001f)
    }
}
