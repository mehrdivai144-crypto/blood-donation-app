package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonorDao {
    @Query("SELECT * FROM donors ORDER BY totalDonations DESC, id DESC")
    fun getAllDonors(): Flow<List<DonorEntity>>

    @Query("SELECT * FROM donors WHERE id = :id")
    suspend fun getDonorById(id: Long): DonorEntity?

    @Query("SELECT * FROM donors WHERE bloodGroup = :bloodGroup ORDER BY totalDonations DESC")
    fun getDonorsByBloodGroup(bloodGroup: String): Flow<List<DonorEntity>>

    @Query("""
        SELECT * FROM donors 
        WHERE (:bloodGroup IS NULL OR bloodGroup = :bloodGroup)
        AND (:query = '' OR name LIKE '%' || :query || '%' OR district LIKE '%' || :query || '%' OR area LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%')
        ORDER BY totalDonations DESC
    """)
    fun searchDonors(query: String, bloodGroup: String?): Flow<List<DonorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonor(donor: DonorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDonors(donors: List<DonorEntity>)

    @Update
    suspend fun updateDonor(donor: DonorEntity)

    @Delete
    suspend fun deleteDonor(donor: DonorEntity)

    @Query("UPDATE donors SET totalDonations = totalDonations + 1, lastDonationDate = :date, lastDonationTimestamp = :timestamp WHERE id = :id")
    suspend fun recordDonationForDonor(id: Long, date: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM donors")
    suspend fun getDonorCount(): Int
}
