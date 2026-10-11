package com.gymrpg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.*
import com.gymrpg.core.designsystem.*
import com.gymrpg.core.integrations.CapabilityDiagnostics
import com.gymrpg.core.integrations.CapabilityFact
import com.gymrpg.core.notifications.NotificationCapability
import com.gymrpg.feature.anatomy.AnatomyDemo
import com.gymrpg.integration.xiaomicn.XiaomiHealthKitProbe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val tabs = listOf("Home", "Workout", "Calendar", "Food", "Profile")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        val preferences = DemoPreferences(this)
        setContent {
            val settings by preferences.settings.collectAsStateWithLifecycle(initialValue = DemoSettings())
            val preferenceError by preferences.error.collectAsStateWithLifecycle()
            val scope = rememberCoroutineScope()
            val capability = rememberVisualCapability()
            val effectiveSettings = settings.copy(
                fullGlass = settings.fullGlass && capability.fullAllowed && !settings.reducedMotion && capability.systemMotion,
                reducedMotion = settings.reducedMotion || !capability.systemMotion,
            )
            val dark = when (settings.theme) { "Light" -> false; "System" -> isSystemInDarkTheme(); else -> true }
            SideEffect {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }
            GymRpgTheme(fullGlass = effectiveSettings.fullGlass, useSystemFont = settings.systemFont, reducedMotion = effectiveSettings.reducedMotion, colorMode = when(settings.theme) { "Light" -> GymColorMode.LIGHT; "System" -> GymColorMode.SYSTEM; else -> GymColorMode.DARK }) {
                M0Shell(settings, effectiveSettings, capability.reason, preferenceError, onSettingsChanged = { value -> scope.launch { preferences.save(value) } })
            }
        }
    }
}

@Composable
private fun M0Shell(settings: DemoSettings, effective: DemoSettings, fallbackReason: String, preferenceError: String?, onSettingsChanged: (DemoSettings) -> Unit) {
    val navigation = rememberNavController()
    val entry by navigation.currentBackStackEntryAsState()
    val selected = entry?.destination?.route ?: "Home"
    Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
        M0Header(settings, effective, fallbackReason, preferenceError, onSettingsChanged)
        NavHost(navigation, startDestination = "Home", modifier = Modifier.weight(1f)) {
            tabs.forEach { tab ->
                composable(tab) {
                    LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        item { Text(tab, Modifier.testTag("screen_title"), style = MaterialTheme.typography.headlineMedium) }
                        when (tab) {
                            "Home" -> item { AnatomyDemo(reducedMotion = effective.reducedMotion) }
                            "Profile" -> {
                                item { ProfilePreferences(settings, onSettingsChanged) }
                                item { PlatformDiagnostics() }
                            }
                            else -> item { GymGlassSurface { Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Your ${tab.lowercase()} space", style = MaterialTheme.typography.titleLarge)
                                Text("This is the M0 navigation proof. ${when(tab) { "Workout" -> "Workout logging arrives in M2."; "Calendar" -> "Planning and template rotation arrive in M3."; else -> "Nutrition logging arrives in M5." }}")
                            } } }
                        }
                    }
                }
            }
        }
        GymPillNav(tabs = tabs, selectedIndex = tabs.indexOf(selected).coerceAtLeast(0), onSelected = { index ->
            navigation.navigate(tabs[index]) {
                popUpTo(navigation.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}

@Composable
private fun M0Header(settings: DemoSettings, effective: DemoSettings, fallbackReason: String, preferenceError: String?, onSettingsChanged: (DemoSettings) -> Unit) {
    Column(Modifier.fillMaxWidth().testTag("m0_header").padding(horizontal = 20.dp, vertical = 8.dp)) {
        Text("GYM RPG", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(if (effective.fullGlass) "Full Glass" else "Efficient Glass", Modifier.testTag(if(effective.fullGlass) "glass_full" else "glass_efficient"), style = MaterialTheme.typography.labelLarge)
            Text("M0 · OFFLINE", style = MaterialTheme.typography.labelMedium)
        }
        if (settings.fullGlass && !effective.fullGlass) Text("Efficient fallback · ${if(effective.reducedMotion) "Reduced motion" else fallbackReason}", style = MaterialTheme.typography.bodySmall)
        preferenceError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.testTag("preferences_error")) }
        FilterChip(settings.fullGlass, { onSettingsChanged(settings.copy(fullGlass = !settings.fullGlass)) }, modifier = Modifier.testTag("glass_toggle").heightIn(min = 48.dp), label = { Text("Full Glass") })
    }
}

@Composable
private fun ProfilePreferences(settings: DemoSettings, onSettingsChanged: (DemoSettings) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(text, { text = it }, modifier = Modifier.fillMaxWidth().testTag("ime_probe"), label = { Text("Keyboard inset check") }, supportingText = { Text("Temporary demo text; no profile is created.") })
        Text("Appearance", style = MaterialTheme.typography.titleLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Dark", "Light", "System").forEach { mode -> FilterChip(settings.theme == mode, { onSettingsChanged(settings.copy(theme = mode)) }, label = { Text(mode) }) }
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Reduced motion", Modifier.weight(1f))
            Switch(settings.reducedMotion, { onSettingsChanged(settings.copy(reducedMotion = it)) })
        }
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Use system font", Modifier.weight(1f))
            Switch(settings.systemFont, { onSettingsChanged(settings.copy(systemFont = it)) })
        }
        Text("Google Sans Flex • SIL Open Font License 1.1", style = MaterialTheme.typography.bodySmall)
        var notices by remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        OutlinedButton(onClick = { scope.launch { notices = withContext(Dispatchers.IO) {
            fun readNotices(path: String): List<String> = context.assets.list(path).orEmpty().flatMap { name ->
                val child = "$path/$name"
                if (context.assets.list(child).orEmpty().isNotEmpty()) readNotices(child)
                else listOf("$child\n" + context.assets.open(child).bufferedReader().use { it.readText() })
            }
            readNotices("licenses").joinToString("\n\n")
        } } }) { Text("Third-party licenses") }
        notices?.let { body -> AlertDialog(onDismissRequest = { notices = null }, confirmButton = { TextButton(onClick = { notices = null }) { Text("Close") } }, title = { Text("Licenses") }, text = { androidx.compose.foundation.lazy.LazyColumn { item { Text(body) } } }) }
    }
}

@Composable
private fun PlatformDiagnostics() {
    val context = LocalContext.current
    var facts by remember { mutableStateOf<List<CapabilityFact>>(emptyList()) }
    LaunchedEffect(context) {
        val base = CapabilityDiagnostics.read(context)
        facts = base + withContext(Dispatchers.IO) { listOf(XiaomiHealthKitProbe.read(context), CapabilityFact("Live Updates", NotificationCapability.read(context))) }
    }
    val density = LocalDensity.current
    val top = WindowInsets.statusBars.getTop(density)
    val bottom = WindowInsets.navigationBars.getBottom(density)
    val ime = WindowInsets.ime.getBottom(density)
    val cutout = WindowInsets.displayCutout.getTop(density)
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Device diagnostics", style = MaterialTheme.typography.titleLarge)
        Text("Measured insets (px): status $top · cutout $cutout · navigation $bottom · IME $ime")
        facts.forEach { fact -> GymGlassSurface { Column(Modifier.padding(16.dp)) {
            Text(fact.label, style = MaterialTheme.typography.titleMedium)
            Text(fact.detail, style = MaterialTheme.typography.bodyMedium)
        } } }
        Text("HealthKit package presence is not developer authorization. HyperIsland presentation is controlled by Xiaomi and is unverified. M0 posts no workout notifications and reads no health data.", style = MaterialTheme.typography.bodySmall)
    }
}
