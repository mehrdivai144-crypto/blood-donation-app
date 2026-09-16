package com.example.data

import kotlinx.coroutines.flow.Flow

class BloodDonationRepository(
    private val donorDao: DonorDao,
    private val recordDao: DonationRecordDao,
    private val emergencyRequestDao: EmergencyRequestDao
) {
    // Donors
    val allDonors: Flow<List<DonorEntity>> = donorDao.getAllDonors()

    fun searchDonors(query: String, bloodGroup: String?): Flow<List<DonorEntity>> {
        return donorDao.searchDonors(query.trim(), bloodGroup?.takeIf { it.isNotEmpty() && it != "সব" })
    }

    suspend fun getDonorById(id: Long): DonorEntity? = donorDao.getDonorById(id)

    suspend fun insertDonor(donor: DonorEntity): Long = donorDao.insertDonor(donor)

    suspend fun updateDonor(donor: DonorEntity) = donorDao.updateDonor(donor)

    suspend fun deleteDonor(donor: DonorEntity) = donorDao.deleteDonor(donor)

    suspend fun recordDonationForDonor(id: Long, date: String, timestamp: Long) {
        donorDao.recordDonationForDonor(id, date, timestamp)
    }

    // Records
    val allDonationRecords: Flow<List<DonationRecordEntity>> = recordDao.getAllRecords()

    fun getRecordsForDonor(donorId: Long): Flow<List<DonationRecordEntity>> {
        return recordDao.getRecordsForDonor(donorId)
    }

    suspend fun insertDonationRecord(record: DonationRecordEntity): Long {
        val recordId = recordDao.insertRecord(record)
        // Automatically increment the donor's total donation count & update last donation date
        donorDao.recordDonationForDonor(record.donorId, record.donationDate, record.timestamp)
        return recordId
    }

    // Emergency Requests
    val allEmergencyRequests: Flow<List<EmergencyRequestEntity>> = emergencyRequestDao.getAllRequests()
    val activeRequestsCount: Flow<Int> = emergencyRequestDao.getActiveRequestsCount()

    suspend fun insertEmergencyRequest(request: EmergencyRequestEntity): Long {
        return emergencyRequestDao.insertRequest(request)
    }

    suspend fun updateRequestStatus(id: Long, newStatus: String) {
        emergencyRequestDao.updateStatus(id, newStatus)
    }

    suspend fun deleteEmergencyRequest(request: EmergencyRequestEntity) {
        emergencyRequestDao.deleteRequest(request)
    }
}
