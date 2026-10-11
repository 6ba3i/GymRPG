package com.gymrpg.integration.xiaomicn

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.gymrpg.core.integrations.CapabilityFact

/** Package visibility is the only M0 Xiaomi probe. No SDK, binding, credentials, or health reads. */
object XiaomiHealthKitProbe {
    const val HEALTHKIT_PACKAGE = "com.xiaomi.healthkit"

    internal fun description(visible: Boolean): String = if (visible) {
        "Package visible; developer service approval, authorized scopes, signing identity, and data access unverified"
    } else {
        "Package not visible or not installed; developer authorization required before integration"
    }

    fun read(context: Context): CapabilityFact {
        val visible = try {
            if (Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(HEALTHKIT_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(HEALTHKIT_PACKAGE, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
        return CapabilityFact("Mainland HealthKit", description(visible))
    }
}
