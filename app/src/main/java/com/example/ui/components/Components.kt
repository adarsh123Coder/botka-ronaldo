package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Priority
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.theme.brush

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    borderColor: Color? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.90f),
    content: @Composable () -> Unit
) {
    val currentTheme = LocalAppTheme.current
    val borderBrush = if (borderColor != null) {
        Brush.verticalGradient(
            colors = listOf(
                borderColor.copy(alpha = 0.8f),
                borderColor.copy(alpha = 0.3f),
                Color.Black.copy(alpha = 0.2f)
            )
        )
    } else {
        currentTheme.bevelBorderBrush
    }

    Surface(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = shape,
                ambientColor = currentTheme.primaryColor.copy(alpha = 0.15f),
                spotColor = currentTheme.bevelShadow.copy(alpha = 0.35f)
            )
            .clip(shape)
            .border(BorderStroke(1.2.dp, borderBrush), shape),
        shape = shape,
        color = backgroundColor,
        tonalElevation = 4.dp
    ) {
        Box {
            // Subtle 3D top-glass sheen gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                currentTheme.bevelHighlight.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        )
                    )
            )
            content()
        }
    }
}

@Composable
fun PriorityBadge(priority: Priority, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderColor, label) = when (priority) {
        Priority.URGENT -> Quad(Color(0xFFE11D48).copy(alpha = 0.22f), Color(0xFFFB7185), Color(0xFFF43F5E), "URGENT")
        Priority.HIGH -> Quad(Color(0xFFEA580C).copy(alpha = 0.22f), Color(0xFFFB923C), Color(0xFFF97316), "HIGH")
        Priority.MEDIUM -> Quad(Color(0xFF0284C7).copy(alpha = 0.20f), Color(0xFF38BDF8), Color(0xFF0EA5E9), "MED")
        Priority.LOW -> Quad(Color(0xFF475569).copy(alpha = 0.25f), Color(0xFF94A3B8), Color(0xFF64748B), "LOW")
    }

    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor.copy(alpha = 0.6f)), RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current
    val shape = RoundedCornerShape(14.dp)

    val bgModifier = if (isSelected) {
        Modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        currentTheme.primaryColor,
                        currentTheme.primaryColor.copy(alpha = 0.75f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.6f), Color.Transparent)
                    )
                ),
                shape
            )
    } else {
        Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            .border(
                BorderStroke(1.dp, currentTheme.bevelShadow.copy(alpha = 0.2f)),
                shape
            )
    }

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .shadow(if (isSelected) 4.dp else 1.dp, shape)
            .clip(shape)
            .then(bgModifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            color = if (isSelected) currentTheme.surfaceColor else MaterialTheme.colorScheme.onSurface,
            fontFamily = SpaceGroteskFontFamily,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun StreakBadge(streak: Int, modifier: Modifier = Modifier) {
    val currentTheme = LocalAppTheme.current
    val shape = RoundedCornerShape(10.dp)

    Box(
        modifier = modifier
            .shadow(2.dp, shape)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0284C7).copy(alpha = 0.25f),
                        Color(0xFF0369A1).copy(alpha = 0.15f)
                    )
                )
            )
            .border(BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)), shape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AcUnit,
                contentDescription = "Cold Streak",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${streak}d",
                color = Color(0xFFBAE6FD),
                fontFamily = SyneFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ModernProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 10.dp,
    trackColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
    brush: Brush = LocalAppTheme.current.brush
) {
    val currentTheme = LocalAppTheme.current
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "progress"
    )

    // 3D Glass Tube Cylinder
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
            .border(BorderStroke(1.dp, currentTheme.bevelShadow.copy(alpha = 0.25f)), CircleShape)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(height)
                    .clip(CircleShape)
                    .background(brush)
            ) {
                // Top glossy glass highlight line for 3D depth
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height / 2)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.45f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    }
}

fun getHobbyIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "music", "guitar" -> Icons.Default.Favorite
        "palette", "art" -> Icons.Default.Palette
        "fitness", "run", "gym" -> Icons.Default.FitnessCenter
        "book", "learning" -> Icons.Default.School
        "code", "tech" -> Icons.Default.Terminal
        "mind", "meditation" -> Icons.Default.Psychology
        "diamond", "craft" -> Icons.Default.Diamond
        else -> Icons.Default.LocalFireDepartment
    }
}

@Composable
fun ModernBottomNav(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current
    val navItems = listOf(
        Triple("Tasks", Icons.Filled.FormatListBulleted, Icons.Outlined.FormatListBulleted),
        Triple("Passions", Icons.Filled.LocalFireDepartment, Icons.Filled.LocalFireDepartment),
        Triple("Matrix", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        Triple("Aesthetics", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        tonalElevation = 12.dp,
        border = BorderStroke(1.2.dp, currentTheme.bevelBorderBrush)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = selectedIndex == index
                val animatedColor by animateColorAsState(
                    targetValue = if (isSelected) currentTheme.primaryColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                    label = "navColor"
                )

                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = { onTabSelected(index) }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .then(
                                    if (isSelected) Modifier
                                        .shadow(4.dp, RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(
                                                    currentTheme.primaryColor.copy(alpha = 0.22f),
                                                    currentTheme.primaryColor.copy(alpha = 0.08f)
                                                )
                                            )
                                        )
                                        .border(
                                            BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.5f)),
                                            RoundedCornerShape(14.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 5.dp)
                                    else Modifier.padding(5.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSelected) item.second else item.third,
                                contentDescription = item.first,
                                tint = animatedColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = item.first,
                            color = animatedColor,
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
