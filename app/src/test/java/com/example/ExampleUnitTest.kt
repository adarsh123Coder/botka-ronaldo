package com.example

import com.example.data.model.HobbyItem
import com.example.data.model.MasteryLevel
import com.example.data.model.Priority
import com.example.data.model.TaskItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testTaskSubtaskParsing() {
        val task = TaskItem(
            title = "Sample Task",
            priority = Priority.HIGH,
            subtasksJson = "done:1:First step\ndone:0:Second step"
        )
        val subtasks = task.parseSubtasks()
        assertEquals(2, subtasks.size)
        assertTrue(subtasks[0].isDone)
        assertEquals("First step", subtasks[0].title)
        assertEquals(false, subtasks[1].isDone)
        assertEquals("Second step", subtasks[1].title)
    }

    @Test
    fun testHobbyMasteryCalculation() {
        val noviceHobby = HobbyItem(title = "Piano", totalMinutes = 120) // 2 hrs
        assertEquals(MasteryLevel.NOVICE, noviceHobby.masteryLevel)

        val explorerHobby = HobbyItem(title = "Guitar", totalMinutes = 600) // 10 hrs
        assertEquals(MasteryLevel.EXPLORER, explorerHobby.masteryLevel)

        val practitionerHobby = HobbyItem(title = "Running", totalMinutes = 1500) // 25 hrs
        assertEquals(MasteryLevel.PRACTITIONER, practitionerHobby.masteryLevel)

        val artisanHobby = HobbyItem(title = "Art", totalMinutes = 3600) // 60 hrs
        assertEquals(MasteryLevel.ARTISAN, artisanHobby.masteryLevel)

        val masterHobby = HobbyItem(title = "Coding", totalMinutes = 7200) // 120 hrs
        assertEquals(MasteryLevel.MASTER, masterHobby.masteryLevel)
    }
}
