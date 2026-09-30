package com.example.data.repository

import com.example.data.dao.HobbyDao
import com.example.data.dao.TaskDao
import com.example.data.database.AppDatabase
import com.example.data.model.HobbyItem
import com.example.data.model.HobbyLog
import com.example.data.model.TaskItem
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NovaTaskRepository(
    private val taskDao: TaskDao,
    private val hobbyDao: HobbyDao
) {
    // Tasks Flow
    val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()

    suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun deleteCompletedTasks() = taskDao.deleteCompletedTasks()

    // Hobbies Flow
    val allHobbies: Flow<List<HobbyItem>> = hobbyDao.getAllHobbies()
    val allHobbyLogs: Flow<List<HobbyLog>> = hobbyDao.getAllLogs()

    fun getLogsForHobby(hobbyId: Long): Flow<List<HobbyLog>> = hobbyDao.getLogsForHobby(hobbyId)

    suspend fun insertHobby(hobby: HobbyItem): Long = hobbyDao.insertHobby(hobby)

    suspend fun updateHobby(hobby: HobbyItem) = hobbyDao.updateHobby(hobby)

    suspend fun deleteHobby(hobby: HobbyItem) {
        hobbyDao.deleteLogsForHobby(hobby.id)
        hobbyDao.deleteHobby(hobby)
    }

    suspend fun logHobbySession(hobby: HobbyItem, minutes: Int, notes: String, mood: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        // Calculate new streak
        val isConsecutive = isYesterday(hobby.lastLoggedDate)
        val isTodayAlready = hobby.lastLoggedDate == todayStr

        val newStreak = when {
            isTodayAlready -> hobby.currentStreak // already counted today
            isConsecutive -> hobby.currentStreak + 1
            hobby.lastLoggedDate.isEmpty() -> 1
            else -> 1 // reset streak to 1
        }
        val newLongestStreak = maxOf(hobby.longestStreak, newStreak)

        val updatedHobby = hobby.copy(
            currentStreak = newStreak,
            longestStreak = newLongestStreak,
            totalMinutes = hobby.totalMinutes + minutes,
            totalSessions = hobby.totalSessions + 1,
            lastLoggedDate = todayStr
        )

        hobbyDao.updateHobby(updatedHobby)

        val log = HobbyLog(
            hobbyId = hobby.id,
            date = todayStr,
            minutes = minutes,
            notes = notes,
            mood = mood
        )
        hobbyDao.insertLog(log)
    }

    private fun isYesterday(dateStr: String): Boolean {
        if (dateStr.isBlank()) return false
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val logged = dateFormat.parse(dateStr)?.time ?: return false
            val now = System.currentTimeMillis()
            val diffDays = (now - logged) / 86400000L
            diffDays in 1..1
        } catch (_: Exception) {
            false
        }
    }

    suspend fun resetToSampleData() {
        taskDao.clearAllTasks()
        hobbyDao.clearAllLogs()
        hobbyDao.clearAllHobbies()
        AppDatabase.populateInitialData(taskDao, hobbyDao)
    }
}
