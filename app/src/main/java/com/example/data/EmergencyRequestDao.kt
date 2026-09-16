package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyRequestDao {
    @Query("SELECT * FROM emergency_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE status = 'রক্ত খুঁজছে' ORDER BY timestamp DESC")
    fun getActiveRequests(): Flow<List<EmergencyRequestEntity>>

    @Query("SELECT * FROM emergency_requests WHERE bloodGroup = :bloodGroup ORDER BY timestamp DESC")
    fun getRequestsByBloodGroup(bloodGroup: String): Flow<List<EmergencyRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: EmergencyRequestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<EmergencyRequestEntity>)

    @Update
    suspend fun updateRequest(request: EmergencyRequestEntity)

    @Delete
    suspend fun deleteRequest(request: EmergencyRequestEntity)

    @Query("UPDATE emergency_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT COUNT(*) FROM emergency_requests WHERE status = 'রক্ত খুঁজছে'")
    fun getActiveRequestsCount(): Flow<Int>
}
