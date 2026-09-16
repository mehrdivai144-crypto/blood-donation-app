package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationRecordDao {
    @Query("SELECT * FROM donation_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<DonationRecordEntity>>

    @Query("SELECT * FROM donation_records WHERE donorId = :donorId ORDER BY timestamp DESC")
    fun getRecordsForDonor(donorId: Long): Flow<List<DonationRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DonationRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<DonationRecordEntity>)

    @Query("SELECT COUNT(*) FROM donation_records")
    suspend fun getRecordCount(): Int

    @Query("SELECT SUM(bags) FROM donation_records")
    fun getTotalBagsDonated(): Flow<Int?>
}
