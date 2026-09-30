package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.util.Locale
import com.example.data.model.HobbyItem
import com.example.data.model.HobbyLog
import com.example.ui.components.GlassCard
import com.example.ui.components.ModernProgressBar
import com.example.ui.components.StreakBadge
import com.example.ui.components.getHobbyIcon
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.theme.brush
import com.example.ui.viewmodel.NovaTaskViewModel

@Composable
fun HobbiesScreen(
    viewModel: NovaTaskViewModel,
    modifier: Modifier = Modifier
) {
    val hobbies by viewModel.allHobbies.collectAsStateWithLifecycle()
    val hobbyLogs by viewModel.allHobbyLogs.collectAsStateWithLifecycle()
    val isCreateHobbyOpen by viewModel.isCreateHobbySheetOpen.collectAsStateWithLifecycle()
    val activeHobbyForLog by viewModel.activeHobbyForLog.collectAsStateWithLifecycle()
    val currentTheme = LocalAppTheme.current

    val totalHours = hobbies.sumOf { it.totalMinutes } / 60f
    val maxStreak = hobbies.maxOfOrNull { it.currentStreak } ?: 0

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Stats Card
            item {
                HobbyHeroCard(
                    totalHobbies = hobbies.size,
                    totalHours = totalHours,
                    maxStreak = maxStreak
                )
            }

            // Hobbies Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ACTIVE PASSIONS & HABITS",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.primaryColor,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "${hobbies.size} tracking",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Hobbies List
            if (hobbies.isEmpty()) {
                item {
                    EmptyHobbyState(onAdd = { viewModel.openCreateHobbySheet() })
                }
            } else {
                items(hobbies, key = { it.id }) { hobby ->
                    val logsForHobby = hobbyLogs.filter { it.hobbyId == hobby.id }
                    HobbyCard(
                        hobby = hobby,
                        recentLogs = logsForHobby.take(2),
                        onLogSession = { viewModel.openLogSession(hobby) },
                        onDelete = { viewModel.deleteHobby(hobby) }
                    )
                }
            }

            // Recent Activity Section
            if (hobbyLogs.isNotEmpty()) {
                item {
                    Text(
                        text = "RECENT SESSIONS STREAM",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.secondaryColor,
                        letterSpacing = 1.2.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                }

                items(hobbyLogs.take(5), key = { it.id }) { log ->
                    val parentHobby = hobbies.find { it.id == log.hobbyId }
                    HobbyLogStreamItem(
                        log = log,
                        hobbyTitle = parentHobby?.title ?: "Hobby"
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.openCreateHobbySheet() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 92.dp)
                .testTag("add_hobby_fab"),
            containerColor = currentTheme.primaryColor,
            contentColor = currentTheme.surfaceColor,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Hobby", modifier = Modifier.size(28.dp))
        }

        // Add Hobby Sheet
        if (isCreateHobbyOpen) {
            CreateHobbySheet(
                onDismiss = { viewModel.closeHobbySheet() },
                onSave = { title, cat, icon, color, targetMins, days ->
                    viewModel.saveHobby(title, cat, icon, color, targetMins, days)
                }
            )
        }

        // Log Session Dialog
        activeHobbyForLog?.let { hobby ->
            LogHobbySessionDialog(
                hobby = hobby,
                onDismiss = { viewModel.closeLogSession() },
                onSubmit = { minutes, notes, mood ->
                    viewModel.submitHobbyLog(hobby, minutes, notes, mood)
                }
            )
        }
    }
}

@Composable
fun HobbyHeroCard(
    totalHobbies: Int,
    totalHours: Float,
    maxStreak: Int,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = currentTheme.secondaryColor.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        currentTheme.surfaceColor,
                        currentTheme.surfaceColor.copy(alpha = 0.95f),
                        currentTheme.secondaryColor.copy(alpha = 0.12f)
                    )
                )
            )
            .border(
                BorderStroke(1.2.dp, currentTheme.bevelBorderBrush),
                RoundedCornerShape(26.dp)
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                text = "MASTERY & STREAKS",
                fontFamily = SpaceGroteskFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = currentTheme.secondaryColor,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "Habit & Passion Matrix",
                fontFamily = SyneFontFamily,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Metric 1: Hours
                HobbyMetricItem(
                    label = "Total Hours",
                    value = String.format(Locale.getDefault(), "%.1fh", totalHours),
                    accentColor = currentTheme.primaryColor
                )

                // Metric 2: Max Streak
                HobbyMetricItem(
                    label = "Top Streak",
                    value = "❄️ ${maxStreak}d",
                    accentColor = Color(0xFF38BDF8)
                )

                // Metric 3: Active Passions
                HobbyMetricItem(
                    label = "Active Hobbies",
                    value = "$totalHobbies",
                    accentColor = currentTheme.secondaryColor
                )
            }
        }
    }
}

@Composable
fun HobbyMetricItem(
    label: String,
    value: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontFamily = SyneFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HobbyCard(
    hobby: HobbyItem,
    recentLogs: List<HobbyLog>,
    onLogSession: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current
    var showLogs by remember { mutableStateOf(false) }

    val iconColor = try {
        Color(android.graphics.Color.parseColor(hobby.colorHex))
    } catch (_: Exception) {
        currentTheme.primaryColor
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hobby_card_${hobby.id}"),
        borderColor = iconColor.copy(alpha = 0.40f),
        backgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon, Title, Streak, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3D Beveled Icon Box
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    iconColor.copy(alpha = 0.30f),
                                    iconColor.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.2.dp,
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.6f), iconColor.copy(alpha = 0.3f))
                                )
                            ),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getHobbyIcon(hobby.iconName),
                        contentDescription = hobby.title,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = hobby.title,
                            fontFamily = SyneFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = hobby.masteryLevel.badge,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = hobby.category,
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${hobby.targetMinutesPerSession}m (${hobby.weeklyTargetDays}d/wk)",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                StreakBadge(streak = hobby.currentStreak)

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete Hobby",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Level & Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${hobby.masteryLevel.label} (${String.format(Locale.getDefault(), "%.1f", hobby.totalHours)}h)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = currentTheme.primaryColor
                )
                Text(
                    text = "Next: ${hobby.nextLevelHours.toInt()}h",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            ModernProgressBar(
                progress = hobby.levelProgress,
                height = 6.dp,
                brush = Brush.horizontalGradient(listOf(iconColor, currentTheme.secondaryColor))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer Actions: Log Session button + View history toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Session Count Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showLogs = !showLogs }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hobby.totalSessions} sessions",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Log Session Action Button
                Button(
                    onClick = onLogSession,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = iconColor.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, iconColor.copy(alpha = 0.7f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("log_hobby_btn_${hobby.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "+ Log Session",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expanded Recent Logs
            AnimatedVisibility(visible = showLogs) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "RECENT SESSIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = iconColor,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (recentLogs.isEmpty()) {
                        Text(
                            text = "No sessions logged yet. Tap '+ Log Session' to record your first session!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        recentLogs.forEach { log ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${log.mood} ${log.minutes}m • ${log.date}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (log.notes.isNotBlank()) {
                                    Text(
                                        text = log.notes,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HobbyLogStreamItem(
    log: HobbyLog,
    hobbyTitle: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = log.mood, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = hobbyTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (log.notes.isNotBlank()) {
                        Text(
                            text = log.notes,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${log.minutes} min",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = LocalAppTheme.current.primaryColor
                )
                Text(
                    text = log.date,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun LogHobbySessionDialog(
    hobby: HobbyItem,
    onDismiss: () -> Unit,
    onSubmit: (minutes: Int, notes: String, mood: String) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(hobby.targetMinutesPerSession) }
    var notes by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("🔥") }

    val quickTimes = listOf(15, 30, 45, 60, 90)
    val moods = listOf(
        Pair("🔥", "Flow State"),
        Pair("⚡", "Solid Effort"),
        Pair("🌱", "Gentle")
    )
    val currentTheme = LocalAppTheme.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Session: ${hobby.title}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column {
                Text(
                    text = "Duration",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickTimes.forEach { mins ->
                        val isSelected = selectedMinutes == mins
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) currentTheme.primaryColor
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable { selectedMinutes = mins }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${mins}m",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Vibe & Energy",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    moods.forEach { (emoji, label) ->
                        val isSelected = mood == emoji
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) currentTheme.secondaryColor.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) currentTheme.secondaryColor else Color.Transparent
                                    ),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { mood = emoji }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = emoji, fontSize = 18.sp)
                                Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("What did you focus on? (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedMinutes, notes, mood) },
                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Complete Check-In", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateHobbySheet(
    onDismiss: () -> Unit,
    onSave: (title: String, category: String, iconName: String, colorHex: String, targetMins: Int, daysPerWeek: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Music") }
    var iconName by remember { mutableStateOf("guitar") }
    var colorHex by remember { mutableStateOf("#00F0FF") }
    var targetMinutes by remember { mutableIntStateOf(30) }
    var daysPerWeek by remember { mutableIntStateOf(4) }

    val categories = listOf("Music", "Art", "Fitness", "Learning", "Tech", "Mind", "Craft")
    val icons = listOf(
        Pair("guitar", "Music"),
        Pair("palette", "Art"),
        Pair("run", "Fitness"),
        Pair("book", "Learning"),
        Pair("code", "Tech"),
        Pair("mind", "Mind")
    )
    val colors = listOf("#00F0FF", "#FF007F", "#10B981", "#FFB300", "#8B5CF6", "#FF5722")
    val currentTheme = LocalAppTheme.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Track New Passion / Hobby",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Hobby Name *") },
                placeholder = { Text("e.g. Acoustic Guitar, Watercolor Painting") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hobby_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category
            Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = category == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) currentTheme.primaryColor else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { category = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Icon & Color Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Color swatches
                Column {
                    Text("Accent Color", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colors.forEach { hex ->
                            val color = Color(android.graphics.Color.parseColor(hex))
                            val isSelected = colorHex == hex
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        BorderStroke(2.dp, if (isSelected) Color.White else Color.Transparent),
                                        CircleShape
                                    )
                                    .clickable { colorHex = hex }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Targets: Duration & Days
            Text("Target Session Duration: ${targetMinutes}m", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 30, 45, 60, 90).forEach { mins ->
                    val isSelected = targetMinutes == mins
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) currentTheme.secondaryColor else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { targetMinutes = mins }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Weekly Commitment: $daysPerWeek days / week", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (2..7).forEach { days ->
                    val isSelected = daysPerWeek == days
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) currentTheme.primaryColor else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { daysPerWeek = days }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${days}d",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, category, iconName, colorHex, targetMinutes, daysPerWeek)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_hobby_button"),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor)
            ) {
                Text(
                    text = "Track Passion",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EmptyHobbyState(onAdd: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(LocalAppTheme.current.secondaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = LocalAppTheme.current.secondaryColor,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Hobbies Tracked Yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Track guitar, digital art, fitness, coding, languages, or any craft. Level up with mastery streaks.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onAdd,
            colors = ButtonDefaults.buttonColors(containerColor = LocalAppTheme.current.primaryColor)
        ) {
            Text("+ Add First Passion", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
