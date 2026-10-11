package com.gymrpg

import android.animation.ValueAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.PowerManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

internal data class VisualCapability(val fullAllowed: Boolean = false, val systemMotion: Boolean = true, val reason: String = "Renderer pending")

@Composable
internal fun rememberVisualCapability(): VisualCapability {
    val context = LocalContext.current
    val view = LocalView.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var capability by remember { mutableStateOf(VisualCapability()) }
    DisposableEffect(context, view, lifecycle) {
        val power = context.getSystemService(PowerManager::class.java)
        fun refresh() {
            val reason = when {
                Build.VERSION.SDK_INT < 33 -> "API 33 required for refraction"
                !view.isHardwareAccelerated -> "Hardware renderer unavailable"
                power.isPowerSaveMode -> "Battery saver"
                power.currentThermalStatus >= PowerManager.THERMAL_STATUS_MODERATE -> "Thermal protection"
                else -> "Full renderer available"
            }
            capability = VisualCapability(reason == "Full renderer available", ValueAnimator.areAnimatorsEnabled(), reason)
        }
        val receiver = object : BroadcastReceiver() { override fun onReceive(context: Context?, intent: Intent?) = refresh() }
        val thermal = PowerManager.OnThermalStatusChangedListener { refresh() }
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) refresh() }
        ContextCompat.registerReceiver(context, receiver, IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED), ContextCompat.RECEIVER_NOT_EXPORTED)
        power.addThermalStatusListener(context.mainExecutor, thermal)
        lifecycle.addObserver(observer)
        view.post { refresh() }
        onDispose { context.unregisterReceiver(receiver); power.removeThermalStatusListener(thermal); lifecycle.removeObserver(observer) }
    }
    return capability
}
