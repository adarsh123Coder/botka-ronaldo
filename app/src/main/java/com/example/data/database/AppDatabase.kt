package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.HobbyDao
import com.example.data.dao.TaskDao
import com.example.data.model.HobbyItem
import com.example.data.model.HobbyLog
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [TaskItem::class, HobbyItem::class, HobbyLog::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun hobbyDao(): HobbyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "novatask_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.taskDao(), database.hobbyDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(taskDao: TaskDao, hobbyDao: HobbyDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = dateFormat.format(Date(now))
            val yesterdayStr = dateFormat.format(Date(now - dayMillis))

            // Initial Starter Tasks
            val sampleTasks = listOf(
                TaskItem(
                    title = "Design holographic UI wireframes",
                    description = "Refine the dark mode aesthetic and cyber accents for mobile.",
                    category = "Creative",
                    priority = Priority.URGENT,
                    dueDate = now + dayMillis * 1,
                    isCompleted = false,
                    subtasksJson = "done:1:Sketch typography hierarchy\ndone:0:Export SVG glowing icons\ndone:0:Prototype spring transitions",
                    estimatedMinutes = 45,
                    createdAt = now - 50000
                ),
                TaskItem(
                    title = "Code Room database repository layer",
                    description = "Ensure clean architecture with reactive StateFlow pipeline.",
                    category = "Work",
                    priority = Priority.HIGH,
                    dueDate = now + dayMillis * 2,
                    isCompleted = true,
                    completedAt = now - 10000,
                    subtasksJson = "done:1:Define entities & DAO\ndone:1:Setup Singleton AppDatabase",
                    estimatedMinutes = 30,
                    createdAt = now - 120000
                ),
                TaskItem(
                    title = "High-Intensity Interval Sprint session",
                    description = "Warmup 5m, 8x 30s max effort sprints, cooldown 5m.",
                    category = "Health",
                    priority = Priority.HIGH,
                    dueDate = now,
                    isCompleted = false,
                    subtasksJson = "done:0:Dynamic stretching\ndone:0:Complete 8 intervals\ndone:0:Hydration & foam rolling",
                    estimatedMinutes = 30,
                    createdAt = now - 80000
                ),
                TaskItem(
                    title = "Read Chapter 4 of Clean Architecture",
                    description = "Focus on the Dependency Inversion Principle and boundaries.",
                    category = "Learning",
                    priority = Priority.MEDIUM,
                    dueDate = now + dayMillis * 3,
                    isCompleted = false,
                    subtasksJson = "done:0:Highlight core insights\ndone:0:Write personal summary notes",
                    estimatedMinutes = 40,
                    createdAt = now - 200000
                ),
                TaskItem(
                    title = "Clean and organize studio workspace",
                    description = "Declutter desk, cable management, clean lenses.",
                    category = "Personal",
                    priority = Priority.LOW,
                    dueDate = now + dayMillis * 4,
                    isCompleted = false,
                    subtasksJson = "",
                    estimatedMinutes = 20,
                    createdAt = now - 300000
                )
            )
            taskDao.insertTasks(sampleTasks)

            // Initial Starter Hobbies
            val sampleHobbies = listOf(
                HobbyItem(
                    title = "Electric Guitar & Soloing",
                    category = "Music",
                    iconName = "guitar",
                    colorHex = "#00F0FF", // Electric Cyan
                    targetMinutesPerSession = 30,
                    weeklyTargetDays = 5,
                    currentStreak = 8,
                    longestStreak = 14,
                    totalMinutes = 1420, // ~23.6 hours (Practitioner)
                    totalSessions = 42,
                    lastLoggedDate = todayStr,
                    createdAt = now - dayMillis * 30
                ),
                HobbyItem(
                    title = "Digital Sci-Fi Illustration",
                    category = "Art",
                    iconName = "palette",
                    colorHex = "#FF007F", // Neon Magenta
                    targetMinutesPerSession = 45,
                    weeklyTargetDays = 4,
                    currentStreak = 4,
                    longestStreak = 12,
                    totalMinutes = 980, // ~16.3 hours (Explorer)
                    totalSessions = 22,
                    lastLoggedDate = yesterdayStr,
                    createdAt = now - dayMillis * 25
                ),
                HobbyItem(
                    title = "5K Running & Trail Cardio",
                    category = "Fitness",
                    iconName = "run",
                    colorHex = "#10B981", // Emerald Neon
                    targetMinutesPerSession = 35,
                    weeklyTargetDays = 3,
                    currentStreak = 6,
                    longestStreak = 9,
                    totalMinutes = 750, // ~12.5 hours (Explorer)
                    totalSessions = 24,
                    lastLoggedDate = todayStr,
                    createdAt = now - dayMillis * 20
                ),
                HobbyItem(
                    title = "Japanese Kanji & Grammar",
                    category = "Learning",
                    iconName = "book",
                    colorHex = "#8B5CF6", // Cyber Violet
                    targetMinutesPerSession = 25,
                    weeklyTargetDays = 6,
                    currentStreak = 15,
                    longestStreak = 15,
                    totalMinutes = 1860, // ~31 hours (Practitioner)
                    totalSessions = 68,
                    lastLoggedDate = todayStr,
                    createdAt = now - dayMillis * 45
                ),
                HobbyItem(
                    title = "Game Dev & Shader Coding",
                    category = "Tech",
                    iconName = "code",
                    colorHex = "#FFB300", // Amber Glow
                    targetMinutesPerSession = 60,
                    weeklyTargetDays = 3,
                    currentStreak = 2,
                    longestStreak = 5,
                    totalMinutes = 420, // 7 hours (Explorer)
                    totalSessions = 8,
                    lastLoggedDate = yesterdayStr,
                    createdAt = now - dayMillis * 15
                )
            )
            hobbyDao.insertHobbies(sampleHobbies)

            // Seed a few hobby logs
            val sampleLogs = listOf(
                HobbyLog(
                    hobbyId = 1,
                    date = todayStr,
                    minutes = 35,
                    notes = "Pentatonic runs with metronome at 130 BPM",
                    mood = "🔥",
                    loggedAt = now - 3600000
                ),
                HobbyLog(
                    hobbyId = 3,
                    date = todayStr,
                    minutes = 30,
                    notes = "Morning trail run, great pace and fresh air",
                    mood = "⚡",
                    loggedAt = now - 7200000
                ),
                HobbyLog(
                    hobbyId = 4,
                    date = todayStr,
                    minutes = 25,
                    notes = "Reviewed N4 Kanji flashcards",
                    mood = "🌱",
                    loggedAt = now - 10800000
                )
            )
            hobbyDao.insertLogs(sampleLogs)
        }
    }
}
