package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HobbyItem
import com.example.data.model.HobbyLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HobbyDao {
    @Query("SELECT * FROM hobbies ORDER BY currentStreak DESC, totalMinutes DESC")
    fun getAllHobbies(): Flow<List<HobbyItem>>

    @Query("SELECT * FROM hobbies WHERE id = :id")
    suspend fun getHobbyById(id: Long): HobbyItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHobby(hobby: HobbyItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHobbies(hobbies: List<HobbyItem>)

    @Update
    suspend fun updateHobby(hobby: HobbyItem)

    @Delete
    suspend fun deleteHobby(hobby: HobbyItem)

    @Query("DELETE FROM hobbies WHERE id = :id")
    suspend fun deleteHobbyById(id: Long)

    @Query("DELETE FROM hobbies")
    suspend fun clearAllHobbies()

    // Logs
    @Query("SELECT * FROM hobby_logs WHERE hobbyId = :hobbyId ORDER BY loggedAt DESC")
    fun getLogsForHobby(hobbyId: Long): Flow<List<HobbyLog>>

    @Query("SELECT * FROM hobby_logs ORDER BY loggedAt DESC")
    fun getAllLogs(): Flow<List<HobbyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HobbyLog): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<HobbyLog>)

    @Query("DELETE FROM hobby_logs WHERE hobbyId = :hobbyId")
    suspend fun deleteLogsForHobby(hobbyId: Long)

    @Query("DELETE FROM hobby_logs")
    suspend fun clearAllLogs()
}
