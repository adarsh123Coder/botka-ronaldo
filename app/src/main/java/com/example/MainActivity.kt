package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ModernBottomNav
import com.example.ui.screens.HobbiesScreen
import com.example.ui.screens.InsightsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.NovaTaskTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.viewmodel.NovaTaskViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: NovaTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

            NovaTaskTheme(themeMode = currentTheme) {
                NovaTaskApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NovaTaskApp(viewModel: NovaTaskViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val currentTheme = LocalAppTheme.current

    // BackHandler: Return to Tasks tab if user presses back on secondary tabs
    if (selectedTab != 0) {
        BackHandler {
            viewModel.setSelectedTab(0)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            ModernBottomNav(
                selectedIndex = selectedTab,
                onTabSelected = { viewModel.setSelectedTab(it) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Ultra-Modern Header Bar
            NovaHeaderBar(
                selectedTab = selectedTab,
                onToggleLightDark = { viewModel.toggleQuickLightDark() }
            )

            // Screen Content with Smooth Crossfade
            Crossfade(
                targetState = selectedTab,
                animationSpec = tween(250),
                label = "screen_transition"
            ) { tab ->
                when (tab) {
                    0 -> TasksScreen(viewModel = viewModel)
                    1 -> HobbiesScreen(viewModel = viewModel)
                    2 -> InsightsScreen(viewModel = viewModel)
                    3 -> SettingsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NovaHeaderBar(
    selectedTab: Int,
    onToggleLightDark: () -> Unit
) {
    val currentTheme = LocalAppTheme.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Branding
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    currentTheme.primaryColor.copy(alpha = 0.35f),
                                    currentTheme.primaryColor.copy(alpha = 0.12f)
                                )
                            )
                        )
                        .border(
                            1.2.dp,
                            currentTheme.bevelBorderBrush,
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "NovaTask 3D Crystal Logo",
                        tint = currentTheme.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "NOVATASK",
                        fontFamily = SyneFontFamily,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = when (selectedTab) {
                            0 -> "Cold Focus & Tasks"
                            1 -> "Habit & Passion Matrix"
                            2 -> "Momentum & Analytics"
                            else -> "5 Cold Classy Themes"
                        },
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Light / Dark Mode 3D Beveled Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .shadow(3.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, currentTheme.bevelBorderBrush, RoundedCornerShape(12.dp))
                        .clickable(onClick = onToggleLightDark)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("toggle_light_dark_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (currentTheme.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Light/Dark",
                            tint = currentTheme.primaryColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (currentTheme.isDark) "LIGHT" else "DARK",
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = currentTheme.primaryColor,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}
