package com.gymrpg.feature.anatomy

/** Invert the same fit, center, zoom and pan used by the Canvas renderer. */
internal fun canvasToSvg(
    x: Float, y: Float, width: Float, height: Float,
    zoom: Float, panX: Float, panY: Float, back: Boolean,
): Pair<Float, Float> {
    val fit = minOf(width / 35f, height / 93f)
    val drawnWidth = 35f * fit
    val drawnHeight = 93f * fit
    return ((x - (width - drawnWidth) / 2f - panX - drawnWidth * (1f - zoom) / 2f) / (fit * zoom) + if (back) 37f else 0f) to
        ((y - (height - drawnHeight) / 2f - panY - drawnHeight * (1f - zoom) / 2f) / (fit * zoom))
}

/** Keep the zoomed figure within its viewport; zero when the figure is smaller. */
internal fun panLimits(width: Float, height: Float, zoom: Float): Pair<Float, Float> {
    if (width <= 0f || height <= 0f) return 0f to 0f
    val fit = minOf(width / 35f, height / 93f)
    return maxOf(0f, (35f * fit * zoom - width) / 2f) to maxOf(0f, (93f * fit * zoom - height) / 2f)
}

/** Position a selected source SVG point at the canvas center after zoom. */
internal fun panForSvgFocus(
    sourceX: Float, sourceY: Float, width: Float, height: Float,
    zoom: Float, back: Boolean,
): Pair<Float, Float> {
    val fit = minOf(width / 35f, height / 93f)
    val bodyWidth = 35f * fit
    val bodyHeight = 93f * fit
    val localX = sourceX - if (back) 37f else 0f
    val panX = (17.5f - localX) * fit * zoom
    val panY = (46.5f - sourceY) * fit * zoom
    val (limitX, limitY) = panLimits(width, height, zoom)
    return panX.coerceIn(-limitX, limitX) to panY.coerceIn(-limitY, limitY)
}
