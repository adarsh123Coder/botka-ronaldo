package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.components.GlassCard
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.viewmodel.NovaTaskViewModel

@Composable
fun SettingsScreen(
    viewModel: NovaTaskViewModel,
    modifier: Modifier = Modifier
) {
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val hobbies by viewModel.allHobbies.collectAsStateWithLifecycle()
    val logs by viewModel.allHobbyLogs.collectAsStateWithLifecycle()

    var showResetDialog by remember { mutableStateOf(false) }
    var showClearTasksDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Section: 5 Ultra-Modern Themes
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.ColorLens,
                    contentDescription = null,
                    tint = currentTheme.primaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "5 CLASSY COLD AESTHETICS",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.primaryColor,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Frosted glass, arctic platinum & luxury light/dark palettes",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(AppThemeMode.entries) { themeMode ->
            val isSelected = currentTheme == themeMode
            ThemeSelectionCard(
                themeMode = themeMode,
                isSelected = isSelected,
                onClick = { viewModel.setTheme(themeMode) }
            )
        }

        // Section: Preferences
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Vibration,
                    contentDescription = null,
                    tint = currentTheme.secondaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "PREFERENCES & FEEDBACK",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.secondaryColor,
                    letterSpacing = 1.2.sp
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic Tactile Feedback",
                            fontFamily = SyneFontFamily,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Vibrate on task check-off and streak updates",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.setHapticsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = currentTheme.primaryColor,
                            checkedTrackColor = currentTheme.primaryColor.copy(alpha = 0.4f)
                        )
                    )
                }
            }
        }

        // Section: Data & Storage
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Storage,
                    contentDescription = null,
                    tint = currentTheme.primaryColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "LOCAL DATA & STORAGE (ROOM)",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.primaryColor,
                    letterSpacing = 1.2.sp
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Active Tasks", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${tasks.count { !it.isCompleted }}", fontFamily = SyneFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Completed Tasks", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${tasks.count { it.isCompleted }}", fontFamily = SyneFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tracked Hobbies", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${hobbies.size}", fontFamily = SyneFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Recorded Sessions", fontFamily = SpaceGroteskFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${logs.size}", fontFamily = SyneFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { showClearTasksDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Clear Completed", fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Button(
                            onClick = { showResetDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor.copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp), tint = currentTheme.primaryColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reset Demo", fontFamily = SpaceGroteskFontFamily, fontSize = 11.sp, color = currentTheme.primaryColor, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section: About
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ABOUT NOVATASK",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.2.sp
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NovaTask • Classy Cold 3D Habit & Task Engine",
                        fontFamily = SyneFontFamily,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Precision engineered with 3D tactile depth, Google Syne & Space Grotesk typography, and offline Room Database persistence.",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }

    // Dialogs
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Demo Data?", fontFamily = SyneFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("This will restore starter tasks, passions (guitar, art, running), and session logs for demonstration.", fontFamily = SpaceGroteskFontFamily) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetToDemoData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor)
                ) {
                    Text("Reset", color = currentTheme.surfaceColor, fontFamily = SpaceGroteskFontFamily, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", fontFamily = SpaceGroteskFontFamily)
                }
            }
        )
    }

    if (showClearTasksDialog) {
        AlertDialog(
            onDismissRequest = { showClearTasksDialog = false },
            title = { Text("Clear Completed Tasks?", fontFamily = SyneFontFamily, fontWeight = FontWeight.Bold) },
            text = { Text("All finished tasks will be permanently removed from the list.", fontFamily = SpaceGroteskFontFamily) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCompletedTasks()
                        showClearTasksDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Clear", color = Color.White, fontFamily = SpaceGroteskFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearTasksDialog = false }) {
                    Text("Cancel", fontFamily = SpaceGroteskFontFamily)
                }
            }
        )
    }
}

@Composable
fun ThemeSelectionCard(
    themeMode: AppThemeMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) themeMode.primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("theme_card_${themeMode.id}"),
        borderColor = borderColor,
        backgroundColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = themeMode.title,
                        fontFamily = SyneFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isSelected) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Active",
                            tint = themeMode.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = themeMode.subtitle,
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Color Swatches
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    themeMode.gradientColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(color)
                                .border(BorderStroke(1.2.dp, Color.White.copy(alpha = 0.4f)), CircleShape)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .shadow(2.dp, CircleShape)
                            .clip(CircleShape)
                            .background(themeMode.surfaceColor)
                            .border(BorderStroke(1.2.dp, Color.White.copy(alpha = 0.3f)), CircleShape)
                    )
                }
            }

            // Mode Tag (Dark / Light) with 3D Bevel
            Box(
                modifier = Modifier
                    .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) themeMode.primaryColor.copy(alpha = 0.20f)
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isSelected) themeMode.primaryColor else Color.Transparent
                        ),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (themeMode.isDark) "DARK" else "LIGHT",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) themeMode.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
