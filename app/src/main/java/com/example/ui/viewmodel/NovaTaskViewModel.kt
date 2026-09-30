package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.HobbyItem
import com.example.data.model.HobbyLog
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import com.example.data.repository.NovaTaskRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskStatusFilter {
    ALL, ACTIVE, COMPLETED
}

class NovaTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("novatask_prefs", Context.MODE_PRIVATE)
    private val repository: NovaTaskRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = NovaTaskRepository(db.taskDao(), db.hobbyDao())
    }

    // UI state flows
    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHobbies: StateFlow<List<HobbyItem>> = repository.allHobbies
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHobbyLogs: StateFlow<List<HobbyLog>> = repository.allHobbyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters & Navigation
    val selectedTab = MutableStateFlow(0) // 0: Tasks, 1: Hobbies, 2: Insights, 3: Settings
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedPriority = MutableStateFlow<Priority?>(null)
    val taskStatusFilter = MutableStateFlow(TaskStatusFilter.ALL)

    // Active Theme
    private val savedThemeId = sharedPrefs.getString("app_theme", AppThemeMode.GLACIAL_TITANIUM.id)
    val currentTheme = MutableStateFlow(
        AppThemeMode.entries.find { it.id == savedThemeId } ?: AppThemeMode.GLACIAL_TITANIUM
    )

    fun toggleQuickLightDark() {
        val next = if (currentTheme.value.isDark) {
            AppThemeMode.NORDIC_ICE
        } else {
            AppThemeMode.GLACIAL_TITANIUM
        }
        setTheme(next)
    }

    // Haptics setting
    val hapticsEnabled = MutableStateFlow(sharedPrefs.getBoolean("haptics_enabled", true))

    // Modals
    val isCreateTaskSheetOpen = MutableStateFlow(false)
    val editingTask = MutableStateFlow<TaskItem?>(null)

    val isCreateHobbySheetOpen = MutableStateFlow(false)
    val activeHobbyForLog = MutableStateFlow<HobbyItem?>(null)

    // Filtered tasks flow
    val filteredTasks: StateFlow<List<TaskItem>> = combine(
        allTasks,
        searchQuery,
        selectedCategory,
        selectedPriority,
        taskStatusFilter
    ) { tasks, query, category, priority, statusFilter ->
        tasks.filter { task ->
            val matchesQuery = query.isBlank() ||
                    task.title.contains(query, ignoreCase = true) ||
                    task.description.contains(query, ignoreCase = true) ||
                    task.category.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || task.category.equals(category, ignoreCase = true)
            val matchesPriority = priority == null || task.priority == priority
            val matchesStatus = when (statusFilter) {
                TaskStatusFilter.ALL -> true
                TaskStatusFilter.ACTIVE -> !task.isCompleted
                TaskStatusFilter.COMPLETED -> task.isCompleted
            }

            matchesQuery && matchesCategory && matchesPriority && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedTab(index: Int) {
        selectedTab.value = index
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSelectedCategory(category: String) {
        selectedCategory.value = category
    }

    fun setSelectedPriority(priority: Priority?) {
        selectedPriority.value = priority
    }

    fun setTaskStatusFilter(filter: TaskStatusFilter) {
        taskStatusFilter.value = filter
    }

    fun setTheme(theme: AppThemeMode) {
        currentTheme.value = theme
        sharedPrefs.edit().putString("app_theme", theme.id).apply()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        hapticsEnabled.value = enabled
        sharedPrefs.edit().putBoolean("haptics_enabled", enabled).apply()
    }

    // Task Actions
    fun toggleTaskComplete(task: TaskItem) {
        viewModelScope.launch {
            val updated = task.copy(
                isCompleted = !task.isCompleted,
                completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun toggleSubTask(task: TaskItem, subTaskIndex: Int) {
        val currentSubtasks = task.parseSubtasks().toMutableList()
        if (subTaskIndex in currentSubtasks.indices) {
            val item = currentSubtasks[subTaskIndex]
            currentSubtasks[subTaskIndex] = item.copy(isDone = !item.isDone)
            val serialized = currentSubtasks.joinToString("\n") { "done:${if (it.isDone) 1 else 0}:${it.title}" }
            viewModelScope.launch {
                repository.updateTask(task.copy(subtasksJson = serialized))
            }
        }
    }

    fun saveTask(
        title: String,
        description: String,
        category: String,
        priority: Priority,
        dueDate: Long?,
        estimatedMinutes: Int,
        subtaskTitles: List<String>,
        existingId: Long?
    ) {
        viewModelScope.launch {
            val subtasksJson = subtaskTitles.filter { it.isNotBlank() }
                .joinToString("\n") { "done:0:$it" }

            if (existingId != null && existingId > 0) {
                val current = allTasks.value.find { it.id == existingId }
                if (current != null) {
                    val updated = current.copy(
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        priority = priority,
                        dueDate = dueDate,
                        estimatedMinutes = estimatedMinutes,
                        subtasksJson = if (subtaskTitles.isNotEmpty()) subtasksJson else current.subtasksJson
                    )
                    repository.updateTask(updated)
                }
            } else {
                val newTask = TaskItem(
                    title = title.trim(),
                    description = description.trim(),
                    category = category,
                    priority = priority,
                    dueDate = dueDate,
                    estimatedMinutes = estimatedMinutes,
                    subtasksJson = subtasksJson
                )
                repository.insertTask(newTask)
            }
            closeTaskSheet()
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            repository.deleteCompletedTasks()
        }
    }

    fun openCreateTaskSheet(task: TaskItem? = null) {
        editingTask.value = task
        isCreateTaskSheetOpen.value = true
    }

    fun closeTaskSheet() {
        isCreateTaskSheetOpen.value = false
        editingTask.value = null
    }

    // Hobby Actions
    fun openCreateHobbySheet() {
        isCreateHobbySheetOpen.value = true
    }

    fun closeHobbySheet() {
        isCreateHobbySheetOpen.value = false
    }

    fun saveHobby(
        title: String,
        category: String,
        iconName: String,
        colorHex: String,
        targetMinutes: Int,
        weeklyDays: Int
    ) {
        viewModelScope.launch {
            val newHobby = HobbyItem(
                title = title.trim(),
                category = category,
                iconName = iconName,
                colorHex = colorHex,
                targetMinutesPerSession = targetMinutes,
                weeklyTargetDays = weeklyDays
            )
            repository.insertHobby(newHobby)
            closeHobbySheet()
        }
    }

    fun deleteHobby(hobby: HobbyItem) {
        viewModelScope.launch {
            repository.deleteHobby(hobby)
        }
    }

    fun openLogSession(hobby: HobbyItem) {
        activeHobbyForLog.value = hobby
    }

    fun closeLogSession() {
        activeHobbyForLog.value = null
    }

    fun submitHobbyLog(hobby: HobbyItem, minutes: Int, notes: String, mood: String) {
        viewModelScope.launch {
            repository.logHobbySession(hobby, minutes, notes, mood)
            closeLogSession()
        }
    }

    fun resetToDemoData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }
}
