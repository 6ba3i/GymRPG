package com.gymrpg.core.designsystem

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens

/** M0 Full uses pinned Backdrop 1.0.6 behind this module's API; Efficient is shader-free. */
@Composable
fun GymGlassSurface(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val ctx = LocalGlassContext.current
    val shape = RoundedCornerShape(24.dp)
    val tint = if (ctx.dark) Color(0xF0203145) else Color(0xE9E3EFF9)
    val edge = if (ctx.dark) Color(0x80D9E9FF) else Color(0xA0FFFFFF)
    val density = LocalDensity.current
    val glassModifier = if (ctx.mode == GymGlassMode.FULL && ctx.backdrop != null) {
        modifier.drawBackdrop(
            backdrop = ctx.backdrop,
            shape = { shape },
            effects = {
                blur(with(density) { 10.dp.toPx() })
                lens(with(density) { 12.dp.toPx() }, with(density) { 16.dp.toPx() })
            },
            highlight = null,
            shadow = null,
            onDrawSurface = { drawRect(tint.copy(alpha = 0.78f)) }
        ).border(1.dp, edge, shape)
    } else {
        modifier.clip(shape).background(tint).border(1.dp, edge, shape)
    }
    Box(glassModifier, contentAlignment = Alignment.CenterStart) { content() }
}

/** All five destinations are equally sized. The selected lens is separate from the hit areas. */
@Composable
fun GymPillNav(
    tabs: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    require(tabs.isNotEmpty())
    val ctx = LocalGlassContext.current
    val shape = RoundedCornerShape(50)
    BoxWithConstraints(modifier.fillMaxWidth().heightIn(min = 64.dp).testTag("pill_nav")) {
        val slotWidth = maxWidth / tabs.size
        val offset by animateDpAsState(
            targetValue = slotWidth * selectedIndex.coerceIn(0, tabs.lastIndex),
            animationSpec = spring(stiffness = 420f, dampingRatio = 0.78f),
            label = "pill selection"
        )
        GymGlassSurface(Modifier.fillMaxWidth().heightIn(min = 64.dp)) {}
        // Drawing the lens outside the wrapper keeps the selection visible in both modes.
        Box(Modifier
            .offset(x = if (ctx.reducedMotion) slotWidth * selectedIndex.coerceIn(0, tabs.lastIndex) else offset)
            .padding(4.dp)
            .width(slotWidth - 8.dp)
            .height(56.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    if (ctx.dark) listOf(Color(0x619BCFFF), Color(0x2077AADB))
                    else listOf(Color(0xC5FFFFFF), Color(0x7091C8ED))
                )
            )
            .border(1.dp, if (ctx.dark) Color(0x8CCCEBFF) else Color.White, shape))
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp)) {
            tabs.forEachIndexed { index, label ->
                Box(
                    Modifier.width(slotWidth).heightIn(min = 64.dp)
                        .testTag("tab_$label")
                        .clip(shape)
                        .semantics { selected = index == selectedIndex }
                        .clickable(role = Role.Tab, onClickLabel = label) { onSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = if (index == selectedIndex) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun GymGlassButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    GymGlassSurface(modifier.clip(RoundedCornerShape(24.dp)).clickable(onClick = onClick).heightIn(min = 48.dp)) {
        Text(text, modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp), style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun GymGlassSheet(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    GymGlassSurface(modifier.fillMaxWidth(), content)
}
