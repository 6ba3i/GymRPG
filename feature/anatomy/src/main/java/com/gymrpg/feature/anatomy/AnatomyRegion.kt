package com.gymrpg.feature.anatomy

/** A source SVG region and its canonical taxonomy crosswalk. */
data class AnatomyRegion(
    val view: String,
    val pathId: String,
    val label: String,
    val pathData: String,
    val subdivisionId: String?,
    val mappingPrecision: String,
)
