package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Priority
import com.example.data.model.SubTask
import com.example.data.model.TaskItem
import com.example.ui.components.CategoryChip
import com.example.ui.components.GlassCard
import com.example.ui.components.ModernProgressBar
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.LocalAppTheme
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.SyneFontFamily
import com.example.ui.theme.bevelBorderBrush
import com.example.ui.theme.brush
import com.example.ui.viewmodel.NovaTaskViewModel
import com.example.ui.viewmodel.TaskStatusFilter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    viewModel: NovaTaskViewModel,
    modifier: Modifier = Modifier
) {
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedPriority by viewModel.selectedPriority.collectAsStateWithLifecycle()
    val taskStatusFilter by viewModel.taskStatusFilter.collectAsStateWithLifecycle()

    val isSheetOpen by viewModel.isCreateTaskSheetOpen.collectAsStateWithLifecycle()
    val editingTask by viewModel.editingTask.collectAsStateWithLifecycle()
    val currentTheme = LocalAppTheme.current

    val completedCount = allTasks.count { it.isCompleted }
    val totalCount = allTasks.size
    val progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f

    val categories = listOf("All", "Work", "Personal", "Health", "Creative", "Learning")

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Banner Card
            item {
                TaskHeroSection(
                    completedCount = completedCount,
                    totalCount = totalCount,
                    progress = progress
                )
            }

            // Search Bar & Filter Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("task_search_input"),
                        placeholder = { Text("Search tasks, tags, or notes...", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                        )
                    )

                    // Category Chips Horizontal Scroll
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        items(categories) { cat ->
                            CategoryChip(
                                category = cat,
                                isSelected = selectedCategory == cat,
                                onClick = { viewModel.setSelectedCategory(cat) }
                            )
                        }
                    }

                    // Status and Priority Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status filter pills
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TaskStatusFilter.entries.forEach { status ->
                                val isSelected = taskStatusFilter == status
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            BorderStroke(
                                                1.dp,
                                                if (isSelected) MaterialTheme.colorScheme.primary
                                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                            ),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { viewModel.setTaskStatusFilter(status) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = status.name.lowercase().replaceFirstChar { it.uppercase() },
                                        fontFamily = SpaceGroteskFontFamily,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Priority Filter Dropdown
                        PriorityFilterDropdown(
                            selectedPriority = selectedPriority,
                            onSelectPriority = { viewModel.setSelectedPriority(it) }
                        )
                    }
                }
            }

            // Task Items
            if (filteredTasks.isEmpty()) {
                item {
                    EmptyTaskState(
                        isFiltered = searchQuery.isNotEmpty() || selectedCategory != "All" || selectedPriority != null || taskStatusFilter != TaskStatusFilter.ALL,
                        onClearFilters = {
                            viewModel.setSearchQuery("")
                            viewModel.setSelectedCategory("All")
                            viewModel.setSelectedPriority(null)
                            viewModel.setTaskStatusFilter(TaskStatusFilter.ALL)
                        }
                    )
                }
            } else {
                items(filteredTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskComplete(task) },
                        onToggleSubTask = { subIndex -> viewModel.toggleSubTask(task, subIndex) },
                        onEdit = { viewModel.openCreateTaskSheet(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { viewModel.openCreateTaskSheet(null) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 22.dp, bottom = 92.dp)
                .testTag("add_task_fab"),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Task", modifier = Modifier.size(28.dp))
        }

        // Create / Edit Task BottomSheet
        if (isSheetOpen) {
            TaskBottomSheet(
                existingTask = editingTask,
                onDismiss = { viewModel.closeTaskSheet() },
                onSave = { title, desc, cat, prio, due, est, subs ->
                    viewModel.saveTask(title, desc, cat, prio, due, est, subs, editingTask?.id)
                }
            )
        }
    }
}

@Composable
fun TaskHeroSection(
    completedCount: Int,
    totalCount: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val todayDate = dateFormat.format(Date())

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(26.dp), spotColor = currentTheme.primaryColor.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        currentTheme.surfaceColor,
                        currentTheme.surfaceColor.copy(alpha = 0.95f),
                        currentTheme.primaryColor.copy(alpha = 0.12f)
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = todayDate.uppercase(),
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = currentTheme.primaryColor,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Daily Objectives",
                        fontFamily = SyneFontFamily,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 3D Completion Pill
                Box(
                    modifier = Modifier
                        .shadow(4.dp, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    currentTheme.primaryColor.copy(alpha = 0.25f),
                                    currentTheme.primaryColor.copy(alpha = 0.10f)
                                )
                            )
                        )
                        .border(BorderStroke(1.dp, currentTheme.primaryColor.copy(alpha = 0.5f)), RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$completedCount / $totalCount Done",
                        color = currentTheme.primaryColor,
                        fontFamily = SyneFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ModernProgressBar(
                progress = progress,
                height = 10.dp,
                brush = currentTheme.brush
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% of targets completed",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (completedCount == totalCount && totalCount > 0) "All clear! ❄️" else "Frost Focus 🧊",
                    fontFamily = SpaceGroteskFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentTheme.secondaryColor
                )
            }
        }
    }
}

@Composable
fun TaskCard(
    task: TaskItem,
    onToggleComplete: () -> Unit,
    onToggleSubTask: (Int) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentTheme = LocalAppTheme.current
    var isExpanded by remember { mutableStateOf(false) }
    val subtasks = remember(task.subtasksJson) { task.parseSubtasks() }

    val borderColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
    } else {
        when (task.priority) {
            Priority.URGENT -> Color(0xFFF43F5E).copy(alpha = 0.45f)
            Priority.HIGH -> Color(0xFFF97316).copy(alpha = 0.40f)
            else -> null
        }
    }

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("task_item_${task.id}"),
        borderColor = borderColor,
        backgroundColor = if (task.isCompleted) {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // 3D Beveled Checkbox Button
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .shadow(if (task.isCompleted) 4.dp else 1.dp, CircleShape)
                        .clip(CircleShape)
                        .background(
                            if (task.isCompleted) {
                                Brush.verticalGradient(
                                    listOf(
                                        currentTheme.primaryColor,
                                        currentTheme.primaryColor.copy(alpha = 0.75f)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                        Color.Transparent
                                    )
                                )
                            }
                        )
                        .border(
                            BorderStroke(
                                1.5.dp,
                                if (task.isCompleted) Color.White.copy(alpha = 0.8f)
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
                            ),
                            CircleShape
                        )
                        .clickable(onClick = onToggleComplete),
                    contentAlignment = Alignment.Center
                ) {
                    if (task.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = currentTheme.surfaceColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title and description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        fontFamily = SyneFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.isCompleted) {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )

                    if (task.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = task.description,
                            fontFamily = SpaceGroteskFontFamily,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                            maxLines = if (isExpanded) 10 else 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Badges row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PriorityBadge(priority = task.priority)

                        // Category Tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = task.category,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Time estimate
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Estimated Time",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${task.estimatedMinutes}m",
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }

                        if (subtasks.isNotEmpty()) {
                            val doneCount = subtasks.count { it.isDone }
                            Text(
                                text = "[$doneCount/${subtasks.size}]",
                                fontFamily = SyneFontFamily,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = currentTheme.primaryColor
                            )
                        }
                    }
                }

                // Actions (Expand / Options)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Task",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (subtasks.isNotEmpty() || task.description.length > 50) {
                        IconButton(
                            onClick = { isExpanded = !isExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Subtasks checklist if present
            AnimatedVisibility(visible = isExpanded && subtasks.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 42.dp)
                ) {
                    Text(
                        text = "SUBTASKS",
                        fontFamily = SpaceGroteskFontFamily,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    subtasks.forEachIndexed { idx, sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onToggleSubTask(idx) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .shadow(if (sub.isDone) 2.dp else 0.dp, RoundedCornerShape(6.dp))
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (sub.isDone) currentTheme.primaryColor.copy(alpha = 0.85f)
                                        else Color.Transparent
                                    )
                                    .border(
                                        BorderStroke(
                                            1.5.dp,
                                            if (sub.isDone) currentTheme.primaryColor
                                             else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                        ),
                                        RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (sub.isDone) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = currentTheme.surfaceColor,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = sub.title,
                                fontFamily = SpaceGroteskFontFamily,
                                fontSize = 13.sp,
                                color = if (sub.isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface,
                                textDecoration = if (sub.isDone) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityFilterDropdown(
    selectedPriority: Priority?,
    onSelectPriority: (Priority?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                    RoundedCornerShape(8.dp)
                )
                .clickable { expanded = true }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.FilterList,
                contentDescription = "Priority",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = selectedPriority?.label ?: "All Priorities",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Priorities") },
                onClick = {
                    onSelectPriority(null)
                    expanded = false
                }
            )
            Priority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priority.label) },
                    onClick = {
                        onSelectPriority(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyTaskState(
    isFiltered: Boolean,
    onClearFilters: () -> Unit
) {
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
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isFiltered) "No matching tasks found" else "All tasks complete!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isFiltered) "Try resetting search or filters." else "Create a new task to conquer your next ambition.",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )

        if (isFiltered) {
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Clear Filters", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskBottomSheet(
    existingTask: TaskItem?,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDate: Long?,
        estimatedMinutes: Int,
        subtasks: List<String>
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var category by remember { mutableStateOf(existingTask?.category ?: "Personal") }
    var priority by remember { mutableStateOf(existingTask?.priority ?: Priority.MEDIUM) }
    var estimatedMinutes by remember { mutableStateOf(existingTask?.estimatedMinutes ?: 25) }

    val subtasksList = remember {
        mutableStateListOf<String>().apply {
            if (existingTask != null) {
                addAll(existingTask.parseSubtasks().map { it.title })
            }
        }
    }
    var newSubtaskText by remember { mutableStateOf("") }

    val categories = listOf("Work", "Personal", "Creative", "Health", "Learning")
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
                text = if (existingTask != null) "Edit Task" else "Create Modern Task",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("task_title_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Notes & Context (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Category Selector
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
                            .background(
                                if (isSelected) currentTheme.primaryColor
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
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

            // Priority Selector
            Text("Priority", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Priority.entries.forEach { prio ->
                    val isSelected = priority == prio
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) currentTheme.primaryColor.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isSelected) currentTheme.primaryColor else Color.Transparent
                                ),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { priority = prio }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = prio.label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) currentTheme.primaryColor else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Estimated time chips
            Text("Estimated Minutes", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(15, 25, 45, 60, 90).forEach { mins ->
                    val isSelected = estimatedMinutes == mins
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) currentTheme.secondaryColor
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { estimatedMinutes = mins }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${mins}m",
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Subtask adder
            Text("Subtasks / Steps", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSubtaskText,
                    onValueChange = { newSubtaskText = it },
                    placeholder = { Text("Add a step e.g. 'Draft intro'") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (newSubtaskText.isNotBlank()) {
                            subtasksList.add(newSubtaskText.trim())
                            newSubtaskText = ""
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = currentTheme.primaryColor)
                ) {
                    Text("+ Add", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            if (subtasksList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    subtasksList.forEachIndexed { index, sub ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "• $sub", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            IconButton(
                                onClick = { subtasksList.removeAt(index) },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save Button
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, description, category, priority, null, estimatedMinutes, subtasksList)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_task_button"),
                enabled = title.isNotBlank(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = currentTheme.primaryColor,
                    disabledContainerColor = currentTheme.primaryColor.copy(alpha = 0.3f)
                )
            ) {
                Text(
                    text = if (existingTask != null) "Update Task" else "Save Task",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
