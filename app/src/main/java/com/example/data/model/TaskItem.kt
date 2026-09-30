package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority(val label: String, val level: Int) {
    LOW("Low", 1),
    MEDIUM("Medium", 2),
    HIGH("High", 3),
    URGENT("Urgent", 4)
}

@Entity(tableName = "tasks")
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "Personal",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val subtasksJson: String = "", // Stored as newline-delimited "done:0/1:text"
    val estimatedMinutes: Int = 25,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun parseSubtasks(): List<SubTask> {
        if (subtasksJson.isBlank()) return emptyList()
        return subtasksJson.lines().filter { it.isNotBlank() }.mapIndexed { index, line ->
            val parts = line.split(":", limit = 3)
            if (parts.size == 3) {
                SubTask(id = index, isDone = parts[1] == "1", title = parts[2])
            } else {
                SubTask(id = index, isDone = false, title = line)
            }
        }
    }
}

data class SubTask(
    val id: Int,
    val isDone: Boolean,
    val title: String
)
