package com.gymrpg.feature.anatomy

import android.graphics.Matrix
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathParser
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.foundation.gestures.detectTapGestures

private enum class AnatomyView { FRONT, BACK }

/** M0 proof of native front/back SVG rendering with bounded zoom and accessible region list. */
@Composable
fun AnatomyDemo(modifier: Modifier = Modifier, reducedMotion: Boolean = false) {
    var viewName by rememberSaveable { mutableStateOf(AnatomyView.FRONT.name) }
    val view = AnatomyView.valueOf(viewName)
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var scale by rememberSaveable { mutableFloatStateOf(1f) }
    var panX by rememberSaveable { mutableFloatStateOf(0f) }
    var panY by rememberSaveable { mutableFloatStateOf(0f) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val pan = Offset(panX, panY)
    val regions = remember(view) { AnatomyPaths.regions.filter { it.view == view.name.lowercase() } }
    val selected = regions.firstOrNull { it.pathId == selectedId }
    val duration = if (reducedMotion) 0 else 180

    Column(modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FilterChip(
                selected = view == AnatomyView.FRONT,
                onClick = { viewName = AnatomyView.FRONT.name; selectedId = null },
                modifier = Modifier.heightIn(min = 48.dp).testTag("anatomy_front"),
                label = { Text("Front") },
            )
            FilterChip(
                selected = view == AnatomyView.BACK,
                onClick = { viewName = AnatomyView.BACK.name; selectedId = null },
                modifier = Modifier.heightIn(min = 48.dp).testTag("anatomy_back"),
                label = { Text("Back") },
            )
            OutlinedButton(
                onClick = {
                    scale = 1f
                    panX = 0f
                    panY = 0f
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("anatomy_reset"),
                enabled = scale != 1f || panX != 0f || panY != 0f,
            ) { Text("Reset") }
            OutlinedButton(
                onClick = {
                    val region = selected ?: return@OutlinedButton
                    if (canvasSize.width <= 0 || canvasSize.height <= 0) return@OutlinedButton
                    val bounds = android.graphics.RectF()
                    PathParser.createPathFromPathData(region.pathData)?.computeBounds(bounds, true)
                    val zoom = 2.5f
                    val focus = panForSvgFocus(
                        bounds.centerX(), bounds.centerY(),
                        canvasSize.width.toFloat(), canvasSize.height.toFloat(),
                        zoom, view == AnatomyView.BACK,
                    )
                    scale = zoom
                    panX = focus.first
                    panY = focus.second
                },
                modifier = Modifier.heightIn(min = 48.dp).testTag("anatomy_focus"),
                enabled = selected != null && canvasSize.width > 0 && canvasSize.height > 0,
            ) { Text("Zoom to selection") }
        }
        Text("Anatomy map", style = MaterialTheme.typography.titleLarge)
        Text("M0 visual proof • 33 of 52 subdivisions have representative shapes; 19 have list labels only.", style = MaterialTheme.typography.bodySmall)
        Text("Preview colors only • Biceps primary, forearms secondary. No workout data yet.", style = MaterialTheme.typography.bodySmall)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("● Primary", color = Color(0xFFFFC776))
            Text("● Secondary", color = Color(0xFF69CEAE))
            Text("● Selected", color = Color(0xFF73E0FF))
        }
        Crossfade(view, animationSpec = tween(duration), label = "anatomy-view") { shownView ->
            val shownRegions = remember(shownView) { AnatomyPaths.regions.filter { it.view == shownView.name.lowercase() } }
            AnatomyCanvas(
                regions = shownRegions,
                selectedId = selectedId,
                scale = scale,
                pan = pan,
                onTransform = { zoom, delta ->
                    scale = (scale * zoom).coerceIn(1f, 2.5f)
                    val (limitX, limitY) = panLimits(canvasSize.width.toFloat(), canvasSize.height.toFloat(), scale)
                    panX = (panX + delta.x).coerceIn(-limitX, limitX)
                    panY = (panY + delta.y).coerceIn(-limitY, limitY)
                },
                onTap = { selectedId = it },
                interactive = shownView == view,
                modifier = Modifier.fillMaxWidth().height(420.dp).onSizeChanged {
                    if (shownView == view) canvasSize = it
                }.testTag("anatomy_canvas_${shownView.name.lowercase()}").semantics { contentDescription = "${shownView.name.lowercase()} anatomy canvas" },
            )
        }
        if (selected != null) {
            Text("Selected: ${selected.label} • ${selected.subdivisionId ?: "not mapped"} • ${selected.mappingPrecision}", modifier = Modifier.testTag("anatomy_selection"), style = MaterialTheme.typography.bodyMedium)
        }
        HorizontalDivider()
        Text("Accessible regions", style = MaterialTheme.typography.titleMedium)
        LazyColumn(Modifier.height(180.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items(regions.filter { it.subdivisionId != null }, key = { it.pathId }) { region ->
                OutlinedButton(onClick = { selectedId = region.pathId }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).then(if (region == regions.firstOrNull { it.subdivisionId != null }) Modifier.testTag("anatomy_region_front") else Modifier)) {
                    Text(region.label)
                }
            }
            item { Text("List only • no distinct source shape", style = MaterialTheme.typography.titleSmall) }
            items(AnatomyUnrendered.labels) { label ->
                Text(label, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(vertical = 12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun AnatomyCanvas(
    regions: List<AnatomyRegion>, selectedId: String?, scale: Float, pan: Offset,
    onTransform: (Float, Offset) -> Unit, onTap: (String) -> Unit, interactive: Boolean, modifier: Modifier,
) {
    val parsed = remember(regions) {
        regions.map { region -> region to requireNotNull(PathParser.createPathFromPathData(region.pathData)) { "Invalid SVG path: ${region.pathId}" } }
    }
    val latestScale by rememberUpdatedState(scale)
    val latestPan by rememberUpdatedState(pan)
    val latestTap by rememberUpdatedState(onTap)
    val latestTransform by rememberUpdatedState(onTransform)
    Box(modifier.clip(RoundedCornerShape(28.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .35f)).semantics { contentDescription = "Interactive ${regions.firstOrNull()?.view ?: "body"} anatomy map" }) {
        val inputModifier = if (interactive) Modifier.pointerInput(parsed) {
            detectTapGestures { offset -> hitTest(parsed, offset, androidx.compose.ui.geometry.Size(size.width.toFloat(), size.height.toFloat()), latestScale, latestPan)?.let(latestTap) }
        }.pointerInput(parsed) {
            detectTransformGestures { _, panDelta, zoom, _ ->
                val bounded = zoom.coerceIn(.92f, 1.08f)
                latestTransform(bounded, panDelta)
            }
            // A separate tap detector handles gestures shorter than transform touch slop.
        } else Modifier
        Canvas(Modifier.fillMaxSize().then(inputModifier)) {
            drawAnatomy(parsed, selectedId, scale, pan)
        }
        // The 48dp region list supplies accessible targets for thin paths.
    }
}

private fun DrawScope.drawAnatomy(parsed: List<Pair<AnatomyRegion, android.graphics.Path>>, selectedId: String?, scale: Float, pan: Offset) {
    val sx = size.width / 35f
    val sy = size.height / 93f
    val fit = minOf(sx, sy)
    val bodyWidth = 35f * fit
    val bodyHeight = 93f * fit
    val sourceOffset = if (parsed.firstOrNull()?.first?.view == "back") 37f else 0f
    val factor = fit * scale
    val translationX = (size.width - bodyWidth) / 2f + pan.x + bodyWidth * (1f - scale) / 2f - sourceOffset * factor
    val translationY = (size.height - bodyHeight) / 2f + pan.y + bodyHeight * (1f - scale) / 2f
    val matrix = Matrix().apply { setValues(floatArrayOf(factor, 0f, translationX, 0f, factor, translationY, 0f, 0f, 1f)) }
    parsed.forEach { (region, sourcePath) ->
            val androidPath = android.graphics.Path(sourcePath).apply { transform(matrix) }
            val normalized = androidPath.asComposePath()
            val selected = region.pathId == selectedId
            val color = when {
                selected -> Color(0xFF73E0FF)
                region.pathId.startsWith("biceps-") -> Color(0xFFFFC776)
                region.pathId.startsWith("forearm-") -> Color(0xFF69CEAE)
                else -> Color(0xFF72849A)
            }
            drawPath(normalized, color = color.copy(alpha = if (selected) 1f else .78f))
            drawPath(normalized, color = Color(0xFF172331), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
    }
}

private fun hitTest(parsed: List<Pair<AnatomyRegion, android.graphics.Path>>, point: Offset, size: androidx.compose.ui.geometry.Size, scale: Float, pan: Offset): String? {
    if (parsed.isEmpty()) return null
    val (sourceX, sourceY) = canvasToSvg(
        point.x, point.y, size.width, size.height, scale, pan.x, pan.y,
        parsed.first().first.view == "back",
    )
    return parsed.asReversed().firstOrNull { (_, path) ->
        val precision = 100f
        val scaled = android.graphics.Path(path).apply {
            transform(Matrix().apply { setScale(precision, precision) })
        }
        val scaledBounds = android.graphics.RectF().also { scaled.computeBounds(it, true) }
        val region = android.graphics.Region()
        region.setPath(scaled, android.graphics.Region(scaledBounds.toIntRect()))
        region.contains((sourceX * precision).toInt(), (sourceY * precision).toInt())
    }?.first?.pathId
}

private fun android.graphics.RectF.toIntRect() = android.graphics.Rect(left.toInt(), top.toInt(), right.toInt() + 1, bottom.toInt() + 1)
