package com.gymrpg.core.integrations

import android.app.Activity
import android.content.Context
import android.os.Build

/** A read-only observation. Details explicitly distinguish eligibility from authorization. */
data class CapabilityFact(val label: String, val detail: String)

object CapabilityDiagnostics {
    fun read(context: Context): List<CapabilityFact> {
        val accelerated = (context as? Activity)?.window?.decorView?.isHardwareAccelerated
        val graphics = when (accelerated) {
            true -> "Activity window hardware acceleration observed; graphics performance unmeasured"
            false -> "Activity window is not hardware accelerated; use Efficient Glass"
            null -> "Activity renderer not observed; use Efficient Glass until measured"
        }
        return listOf(
            CapabilityFact("Android API", "${Build.VERSION.SDK_INT} (${Build.VERSION.RELEASE})"),
            CapabilityFact("Device", "${Build.MANUFACTURER} ${Build.MODEL}; ROM features unverified"),
            CapabilityFact("Renderer", graphics),
            CapabilityFact("Shader API", if (Build.VERSION.SDK_INT >= 33) "RuntimeShader API available; visual performance unverified" else "RuntimeShader unavailable; use Efficient Glass")
        )
    }
}
