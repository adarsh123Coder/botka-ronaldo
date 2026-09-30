package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.GlassCard
import com.example.ui.components.ModernProgressBar
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.theme.brush
import com.example.ui.viewmodel.NovaTaskViewModel
import java.util.Locale

@Composable
fun InsightsScreen(
    viewModel: NovaTaskViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val hobbies by viewModel.allHobbies.collectAsStateWithLifecycle()
    val logs by viewModel.allHobbyLogs.collectAsStateWithLifecycle()
    val currentTheme = LocalAppTheme.current

    val completedTasks = tasks.count { it.isCompleted }
    val totalTasks = tasks.size
    val taskRate = if (totalTasks > 0) (completedTasks * 100) / totalTasks else 0
    val totalHobbyHours = hobbies.sumOf { it.totalMinutes } / 60f
    val totalSessions = hobbies.sumOf { it.totalSessions }
    val topStreak = hobbies.maxOfOrNull { it.currentStreak } ?: 0

    // Productivity Score (0-100)
    val score = ((taskRate * 0.6f) + ((topStreak * 5).coerceAtMost(40))).toInt().coerceIn(10, 99)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Score Card with 3D Depth
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = currentTheme.primaryColor.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                currentTheme.surfaceColor,
                                currentTheme.surfaceColor.copy(alpha = 0.95f),
                                currentTheme.primaryColor.copy(alpha = 0.15f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.2.dp, currentTheme.bevelBorderBrush),
                        RoundedCornerShape(26.dp)
                    )
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = currentTheme.primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MOMENTUM INDEX",
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primaryColor,
                                letterSpacing = 1.2.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (score > 80) "Optimal Focus" else "Building Rhythm",
                            fontFamily = SyneFontFamily,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$completedTasks tasks completed • $totalSessions sessions logged",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // 3D Circular Score Visualizer
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .shadow(6.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        currentTheme.primaryColor.copy(alpha = 0.25f),
                                        currentTheme.primaryColor.copy(alpha = 0.08f)
                                    )
                                )
                            )
                            .border(BorderStroke(2.5.dp, currentTheme.primaryColor), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$score",
                                fontFamily = SyneFontFamily,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primaryColor
                            )
                            Text(
                                text = "SCORE",
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 7-Day Activity Chart
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKLY ACTIVITY MATRIX",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.primaryColor,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Last 7 Days",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 7-day columns
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val heights = listOf(0.4f, 0.7f, 0.85f, 0.6f, 0.95f, 0.5f, 0.8f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val fillRatio = heights[idx]
                            val isToday = idx == 6
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((100 * fillRatio).dp)
                                        .shadow(if (isToday) 4.dp else 1.dp, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            if (isToday) {
                                                Brush.verticalGradient(
                                                    listOf(currentTheme.primaryColor, currentTheme.secondaryColor)
                                                )
                                            } else {
                                                Brush.verticalGradient(
                                                    listOf(
                                                        currentTheme.primaryColor.copy(alpha = 0.45f),
                                                        currentTheme.primaryColor.copy(alpha = 0.15f)
                                                    )
                                                )
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = day,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontSize = 11.sp,
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) currentTheme.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hobby Category Distribution
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "HOBBY TIME ALLOCATION",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.secondaryColor,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (hobbies.isEmpty()) {
                        Text(
                            "No hobbies tracked yet.",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val hobbiesByCategory = hobbies.groupBy { it.category }
                        val maxCatMinutes = hobbiesByCategory.maxOfOrNull { it.value.sumOf { h -> h.totalMinutes } }?.coerceAtLeast(1) ?: 1

                        hobbiesByCategory.forEach { (cat, list) ->
                            val catMinutes = list.sumOf { it.totalMinutes }
                            val catHours = catMinutes / 60f
                            val progress = catMinutes.toFloat() / maxCatMinutes

                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = cat,
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = String.format(Locale.getDefault(), "%.1f hrs", catHours),
                                        fontFamily = SyneFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = currentTheme.primaryColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                ModernProgressBar(
                                    progress = progress,
                                    height = 7.dp,
                                    brush = currentTheme.brush
                                )
                            }
                        }
                    }
                }
            }
        }

        // Achievements & Badges
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MILESTONES & AWARDS",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            letterSpacing = 1.2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AchievementBadge(
                            icon = "❄️",
                            title = "Frost Titan",
                            desc = "${topStreak}d active streak",
                            isUnlocked = topStreak >= 3,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementBadge(
                            icon = "⚡",
                            title = "Cold Precision",
                            desc = "$completedTasks done",
                            isUnlocked = completedTasks >= 1,
                            modifier = Modifier.weight(1f)
                        )
                        AchievementBadge(
                            icon = "💎",
                            title = "Diamond Mind",
                            desc = "${hobbies.size} passions",
                            isUnlocked = hobbies.size >= 2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Motivational Quote Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(BorderStroke(1.dp, currentTheme.bevelBorderBrush), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = currentTheme.primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "\"Cold concentration and relentless iteration shape the sharpest mind.\"",
                            fontFamily = SyneFontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— Nova Philosophy",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AchievementBadge(
    icon: String,
    title: String,
    desc: String,
    isUnlocked: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha = if (isUnlocked) 1f else 0.4f
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )
            .border(
                BorderStroke(
                    1.dp,
                    if (isUnlocked) LocalAppTheme.current.primaryColor.copy(alpha = 0.4f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ),
                RoundedCornerShape(14.dp)
            )
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
            )
        }
    }
}
