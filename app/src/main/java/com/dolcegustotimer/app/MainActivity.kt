package com.dolcegustotimer.app

import android.content.Context
import androidx.core.content.edit
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private const val STANDARD_SECONDS_PER_BAR = 7f

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DolceGustoTimerApp()
        }
    }
}

private enum class ThemeMode(val label: String) {
    System("System"),
    Light("Light"),
    Dark("Dark")
}

private enum class DrinkFilter(val label: String) {
    All("All"),
    OneCapsule("1 capsule"),
    TwoCapsules("2 capsules")
}

private data class BrewStage(
    val name: String,
    val capsule: String,
    val bars: Int,
    val temperature: String = "Hot"
)

private data class Drink(
    val name: String,
    val family: String,
    val accent: Color,
    val stages: List<BrewStage>
)

private val drinks = listOf(
    Drink("Cappuccino", "Milky classic", Color(0xFFE9A85A), listOf(BrewStage("Milk", "Milk capsule", 6), BrewStage("Coffee", "Coffee capsule", 1))),
    Drink("Latte Macchiato", "Layered milk coffee", Color(0xFFD8B28A), listOf(BrewStage("Milk", "Milk capsule", 5), BrewStage("Coffee", "Coffee capsule", 2))),
    Drink("Cortado", "Small milk coffee", Color(0xFFB9825B), listOf(BrewStage("Milk", "Milk capsule", 3), BrewStage("Coffee", "Coffee capsule", 2))),
    Drink("Flat White", "Creamy coffee", Color(0xFFC68D62), listOf(BrewStage("Milk", "Milk capsule", 4), BrewStage("Coffee", "Coffee capsule", 3))),
    Drink("Chococino", "Hot chocolate", Color(0xFF8C5438), listOf(BrewStage("Milk", "Milk capsule", 5), BrewStage("Chocolate", "Chocolate capsule", 3))),
    Drink("Mocha", "Chocolate coffee", Color(0xFF9D5F43), listOf(BrewStage("Chocolate milk", "Chocolate milk capsule", 5), BrewStage("Coffee", "Coffee capsule", 2))),
    Drink("Espresso", "Short coffee", Color(0xFF6A3B2A), listOf(BrewStage("Coffee", "Coffee capsule", 3))),
    Drink("Espresso Intenso", "Bold short coffee", Color(0xFF5A2E23), listOf(BrewStage("Coffee", "Coffee capsule", 3))),
    Drink("Ristretto Ardenza", "Very intense", Color(0xFF432219), listOf(BrewStage("Coffee", "Coffee capsule", 1))),
    Drink("Caffe Lungo", "Long coffee", Color(0xFF8A5238), listOf(BrewStage("Coffee", "Coffee capsule", 6))),
    Drink("Americano", "Extra long coffee", Color(0xFF9F6845), listOf(BrewStage("Coffee", "Coffee capsule", 7))),
    Drink("Cafe au Lait", "One-capsule milk coffee", Color(0xFFBF8D68), listOf(BrewStage("Coffee & milk", "Cafe au Lait capsule", 6))),
    Drink("Cold Brew Coffee", "Cold capsule", Color(0xFF4C7B87), listOf(BrewStage("Cold coffee", "Cold brew capsule", 7, "Cold"))),
    Drink("Iced Cappuccino", "Cold two-capsule drink", Color(0xFF6FA8AF), listOf(BrewStage("Milk", "Milk capsule", 4, "Cold"), BrewStage("Coffee", "Coffee capsule", 3, "Cold")))
)

@Composable
private fun DolceGustoTimerApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
    var themeMode by rememberSaveable { mutableStateOf(ThemeMode.valueOf(prefs.getString("theme", ThemeMode.System.name) ?: ThemeMode.System.name)) }
    var secondsPerBar by rememberSaveable { mutableFloatStateOf(prefs.getFloat("secondsPerBar", STANDARD_SECONDS_PER_BAR)) }

    DisposableEffect(themeMode, secondsPerBar) {
        prefs.edit {
            putString("theme", themeMode.name)
            putFloat("secondsPerBar", secondsPerBar)
        }
        onDispose { }
    }

    val dark = when (themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (dark) darkPalette() else lightPalette(),
        typography = MaterialTheme.typography
    ) {
        TimerHome(
            themeMode = themeMode,
            onThemeModeChange = { themeMode = it },
            secondsPerBar = secondsPerBar,
            onSecondsPerBarChange = { secondsPerBar = it }
        )
    }
}

private fun lightPalette() = lightColorScheme(
    primary = Color(0xFF7B3F2A),
    secondary = Color(0xFF276461),
    tertiary = Color(0xFFE39B4F),
    background = Color(0xFFFCF8F4),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0E6DE),
    onPrimary = Color.White,
    onBackground = Color(0xFF1F1714)
)

private fun darkPalette() = darkColorScheme(
    primary = Color(0xFFFFB57B),
    secondary = Color(0xFF7DD4CF),
    tertiary = Color(0xFFFFCA7A),
    background = Color(0xFF151211),
    surface = Color(0xFF211B18),
    surfaceVariant = Color(0xFF352A25),
    onPrimary = Color(0xFF3F1B0E),
    onBackground = Color(0xFFF8EEE8)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimerHome(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    secondsPerBar: Float,
    onSecondsPerBarChange: (Float) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(DrinkFilter.All) }
    var showAdvancedSettings by rememberSaveable { mutableStateOf(false) }
    var selectedDrink by remember { mutableStateOf<Drink?>(null) }

    val filtered = drinks.filter { drink ->
        val matchesQuery = drink.name.contains(query, ignoreCase = true) ||
                drink.family.contains(query, ignoreCase = true)
        val matchesFilter = when (filter) {
            DrinkFilter.All -> true
            DrinkFilter.OneCapsule -> drink.stages.size == 1
            DrinkFilter.TwoCapsules -> drink.stages.size > 1
        }
        matchesQuery && matchesFilter
    }

    if (showAdvancedSettings) {
        AdvancedSettingsScreen(
            themeMode = themeMode,
            onThemeModeChange = onThemeModeChange,
            secondsPerBar = secondsPerBar,
            onSecondsPerBarChange = onSecondsPerBarChange,
            onBack = { showAdvancedSettings = false }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                Header(
                    onOpenSettings = { showAdvancedSettings = true }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SearchAndFilters(
                        query = query,
                        onQueryChange = { query = it },
                        filter = filter,
                        onFilterChange = { filter = it }
                    )
                }
                items(filtered, key = { it.name }) { drink ->
                    DrinkCard(
                        drink = drink,
                        secondsPerBar = secondsPerBar,
                        onClick = { selectedDrink = drink }
                    )
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }

    selectedDrink?.let { drink ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { selectedDrink = null },
            sheetState = sheetState
        ) {
            TimerSheet(
                drink = drink,
                secondsPerBar = secondsPerBar,
                onClose = { selectedDrink = null }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedSettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    secondsPerBar: Float,
    onSecondsPerBarChange: (Float) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.systemBars,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .padding(start = 8.dp, end = 18.dp, bottom = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text("Advanced Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SettingsSection(title = "Appearance") {
                    SingleChoiceSegmentedButtonRow {
                        ThemeMode.entries.forEachIndexed { index, mode ->
                            SegmentedButton(
                                selected = themeMode == mode,
                                onClick = { onThemeModeChange(mode) },
                                shape = SegmentedButtonDefaults.itemShape(index, ThemeMode.entries.size)
                            ) {
                                Text(mode.label)
                            }
                        }
                    }
                }
            }
            item {
                SettingsSection(title = "Brew Calibration") {
                    Text(
                        "Standard Dolce Gusto timing: ${STANDARD_SECONDS_PER_BAR.roundToInt()} sec per bar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Adjust only if your machine pours noticeably faster or slower.",
                        modifier = Modifier.padding(top = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Current: ${secondsPerBar.roundToInt()} sec per bar",
                        modifier = Modifier.padding(top = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = secondsPerBar,
                        onValueChange = onSecondsPerBarChange,
                        valueRange = 5f..12f,
                        steps = 6
                    )
                    TextButton(
                        onClick = { onSecondsPerBarChange(STANDARD_SECONDS_PER_BAR) },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Text("Use standard timing", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun Header(
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text("Dolce Gusto Timer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Manual machine brew guide", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Advanced settings")
            }
        }
    }
}

@Composable
private fun SearchAndFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    filter: DrinkFilter,
    onFilterChange: (DrinkFilter) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search cappuccino, cortado, lungo...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DrinkFilter.entries.forEach { item ->
                FilterChip(
                    selected = filter == item,
                    onClick = { onFilterChange(item) },
                    label = { Text(item.label) }
                )
            }
        }
    }
}

@Composable
private fun DrinkCard(drink: Drink, secondsPerBar: Float, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(drink.accent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (drink.stages.size > 1) Icons.Default.Coffee else Icons.Default.LocalCafe,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Text(drink.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(drink.family, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 8.dp)) {
                    drink.stages.forEach {
                        AssistChip(
                            onClick = onClick,
                            label = { Text("${it.name} ${it.bars}") },
                            leadingIcon = { Icon(stageIcon(it), contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            Text(
                formatTime(totalSeconds(drink, secondsPerBar)),
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TimerSheet(drink: Drink, secondsPerBar: Float, onClose: () -> Unit) {
    var stageIndex by rememberSaveable(drink.name) { mutableIntStateOf(0) }
    var remaining by rememberSaveable(drink.name, secondsPerBar) { mutableIntStateOf(stageSeconds(drink.stages[0], secondsPerBar)) }
    var running by rememberSaveable(drink.name) { mutableStateOf(false) }
    val stage = drink.stages[stageIndex]
    val stageDuration = stageSeconds(stage, secondsPerBar)
    val progress by animateFloatAsState(
        targetValue = if (stageDuration == 0) 0f else remaining / stageDuration.toFloat(),
        animationSpec = tween(250),
        label = "timerProgress"
    )

    LaunchedEffect(running, remaining, stageIndex, drink.name) {
        if (running && remaining > 0) {
            delay(1.seconds)
            remaining -= 1
        } else if (running && remaining == 0) {
            if (stageIndex < drink.stages.lastIndex) {
                val nextIndex = stageIndex + 1
                stageIndex = nextIndex
                remaining = stageSeconds(drink.stages[nextIndex], secondsPerBar)
                running = false
            } else {
                running = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 26.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(drink.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(
                    "Stage ${stageIndex + 1} of ${drink.stages.size} - ${stage.capsule}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }
        Spacer(Modifier.height(20.dp))
        TimerRing(progress = progress, accent = drink.accent, text = formatTime(remaining))
        Spacer(Modifier.height(18.dp))
        Text(stage.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("${stage.bars} bars - ${stage.temperature}", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = { running = !running }) {
                Icon(if (running) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Text(if (running) "Pause" else "Start", modifier = Modifier.padding(start = 8.dp))
            }
            TextButton(onClick = {
                running = false
                remaining = stageDuration
            }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Text("Reset", modifier = Modifier.padding(start = 8.dp))
            }
            TextButton(onClick = {
                running = false
                stageIndex = 0
                remaining = stageSeconds(drink.stages.first(), secondsPerBar)
            }) {
                Icon(Icons.Default.Stop, contentDescription = null)
                Text("Stop", modifier = Modifier.padding(start = 8.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            drink.stages.forEachIndexed { index, item ->
                StageRow(
                    stage = item,
                    active = index == stageIndex,
                    done = index < stageIndex,
                    seconds = stageSeconds(item, secondsPerBar)
                )
            }
        }
    }
}

@Composable
private fun TimerRing(progress: Float, accent: Color, text: String) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(220.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
            drawCircle(
                color = Color.Gray.copy(alpha = 0.18f),
                radius = size.minDimension / 2f - stroke.width,
                style = stroke
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = stroke,
                topLeft = Offset(stroke.width, stroke.width),
                size = androidx.compose.ui.geometry.Size(size.width - stroke.width * 2, size.height - stroke.width * 2)
            )
        }
        AnimatedContent(targetState = text, label = "countdownText") { value ->
            Text(value, fontSize = 46.sp, fontWeight = FontWeight.Black, letterSpacing = 0.sp)
        }
    }
}

@Composable
private fun StageRow(stage: BrewStage, active: Boolean, done: Boolean, seconds: Int) {
    val color = when {
        active -> MaterialTheme.colorScheme.primary
        done -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(stageIcon(stage), contentDescription = null, tint = color)
        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(stage.name, color = color, fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
            Text(stage.capsule, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text("${stage.bars} bars - ${formatTime(seconds)}", color = color, fontWeight = FontWeight.Bold)
    }
}

private fun stageIcon(stage: BrewStage): ImageVector =
    if (stage.temperature == "Cold") Icons.Default.WaterDrop else if (stage.name.contains("Milk", ignoreCase = true)) Icons.Default.WaterDrop else Icons.Default.Coffee

private fun totalSeconds(drink: Drink, secondsPerBar: Float): Int =
    drink.stages.sumOf { stageSeconds(it, secondsPerBar) }

private fun stageSeconds(stage: BrewStage, secondsPerBar: Float): Int =
    (stage.bars * secondsPerBar).roundToInt().coerceAtLeast(1)

private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val rest = seconds % 60
    return "%d:%02d".format(minutes, rest)
}
