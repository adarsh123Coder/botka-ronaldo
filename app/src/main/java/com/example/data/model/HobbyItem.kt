package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hobbies")
data class HobbyItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String = "Creative", // Music, Art, Fitness, Learning, Tech, Mind
    val iconName: String = "star", // music, palette, fitness, book, code, run, guitar, game
    val colorHex: String = "#00F0FF",
    val targetMinutesPerSession: Int = 30,
    val weeklyTargetDays: Int = 4,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
    val lastLoggedDate: String = "", // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalHours: Float
        get() = totalMinutes / 60f

    val masteryLevel: MasteryLevel
        get() = when {
            totalHours >= 100f -> MasteryLevel.MASTER
            totalHours >= 50f -> MasteryLevel.ARTISAN
            totalHours >= 20f -> MasteryLevel.PRACTITIONER
            totalHours >= 5f -> MasteryLevel.EXPLORER
            else -> MasteryLevel.NOVICE
        }

    val nextLevelHours: Float
        get() = when (masteryLevel) {
            MasteryLevel.NOVICE -> 5f
            MasteryLevel.EXPLORER -> 20f
            MasteryLevel.PRACTITIONER -> 50f
            MasteryLevel.ARTISAN -> 100f
            MasteryLevel.MASTER -> 200f
        }

    val levelProgress: Float
        get() = (totalHours / nextLevelHours).coerceIn(0f, 1f)
}

enum class MasteryLevel(val label: String, val badge: String) {
    NOVICE("Novice", "🌱"),
    EXPLORER("Explorer", "⚡"),
    PRACTITIONER("Practitioner", "🔥"),
    ARTISAN("Artisan", "💎"),
    MASTER("Grandmaster", "👑")
}

@Entity(tableName = "hobby_logs")
data class HobbyLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hobbyId: Long,
    val date: String, // YYYY-MM-DD
    val minutes: Int,
    val notes: String = "",
    val mood: String = "🔥", // 🔥 On Fire, ⚡ Solid, 🌱 Gentle
    val loggedAt: Long = System.currentTimeMillis()
)
