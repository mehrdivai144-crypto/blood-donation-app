package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BloodDonationRepository
import com.example.data.BloodType
import com.example.data.DonationRecordEntity
import com.example.data.DonorEntity
import com.example.data.EmergencyRequestEntity
import com.example.notification.EmergencyNotificationHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BloodViewModel(
    private val repository: BloodDonationRepository
) : ViewModel() {

    // Tab state: 0=Donors, 1=Emergency Requests, 2=Compatibility, 3=History
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Search and filters for Donors
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBloodFilter = MutableStateFlow("সব")
    val selectedBloodFilter: StateFlow<String> = _selectedBloodFilter.asStateFlow()

    private val _onlyAvailableFilter = MutableStateFlow(false)
    val onlyAvailableFilter: StateFlow<Boolean> = _onlyAvailableFilter.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setBloodFilter(group: String) {
        _selectedBloodFilter.value = group
    }

    fun toggleOnlyAvailable(onlyAvailable: Boolean) {
        _onlyAvailableFilter.value = onlyAvailable
    }

    // Filtered Donors
    @OptIn(ExperimentalCoroutinesApi::class)
    val donors: StateFlow<List<DonorEntity>> = combine(
        _searchQuery,
        _selectedBloodFilter,
        _onlyAvailableFilter
    ) { query, bloodGroup, onlyAvailable ->
        Triple(query, bloodGroup, onlyAvailable)
    }.flatMapLatest { (query, bloodGroup, onlyAvailable) ->
        val group = if (bloodGroup == "সব") null else bloodGroup
        repository.searchDonors(query, group).combine(_onlyAvailableFilter) { list, availableOnly ->
            if (availableOnly) list.filter { it.isAvailable } else list
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Emergency Requests
    val emergencyRequests: StateFlow<List<EmergencyRequestEntity>> = repository.allEmergencyRequests
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeEmergencyCount: StateFlow<Int> = repository.activeRequestsCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Donation History Records
    val donationRecords: StateFlow<List<DonationRecordEntity>> = repository.allDonationRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Blood Compatibility State
    private val _compatibilitySelectedGroup = MutableStateFlow(BloodType.O_POSITIVE)
    val compatibilitySelectedGroup: StateFlow<BloodType> = _compatibilitySelectedGroup.asStateFlow()

    private val _testDonorGroup = MutableStateFlow(BloodType.O_POSITIVE)
    val testDonorGroup: StateFlow<BloodType> = _testDonorGroup.asStateFlow()

    private val _testRecipientGroup = MutableStateFlow(BloodType.A_POSITIVE)
    val testRecipientGroup: StateFlow<BloodType> = _testRecipientGroup.asStateFlow()

    fun setCompatibilitySelectedGroup(type: BloodType) {
        _compatibilitySelectedGroup.value = type
    }

    fun setTestDonorGroup(type: BloodType) {
        _testDonorGroup.value = type
    }

    fun setTestRecipientGroup(type: BloodType) {
        _testRecipientGroup.value = type
    }

    // Dialog & Interaction states
    private val _showAddDonorDialog = MutableStateFlow(false)
    val showAddDonorDialog: StateFlow<Boolean> = _showAddDonorDialog.asStateFlow()

    private val _donorForLogging = MutableStateFlow<DonorEntity?>(null)
    val donorForLogging: StateFlow<DonorEntity?> = _donorForLogging.asStateFlow()

    private val _donorForDetails = MutableStateFlow<DonorEntity?>(null)
    val donorForDetails: StateFlow<DonorEntity?> = _donorForDetails.asStateFlow()

    private val _showEmergencyDialog = MutableStateFlow(false)
    val showEmergencyDialog: StateFlow<Boolean> = _showEmergencyDialog.asStateFlow()

    fun openAddDonorDialog() { _showAddDonorDialog.value = true }
    fun closeAddDonorDialog() { _showAddDonorDialog.value = false }

    fun openLogDonationDialog(donor: DonorEntity) { _donorForLogging.value = donor }
    fun closeLogDonationDialog() { _donorForLogging.value = null }

    fun openDonorDetails(donor: DonorEntity) { _donorForDetails.value = donor }
    fun closeDonorDetails() { _donorForDetails.value = null }

    fun openEmergencyDialog() { _showEmergencyDialog.value = true }
    fun closeEmergencyDialog() { _showEmergencyDialog.value = false }

    // Business actions
    fun registerDonor(
        name: String,
        bloodGroup: String,
        phone: String,
        district: String,
        area: String,
        totalDonations: Int,
        lastDonationDate: String,
        notes: String
    ) {
        viewModelScope.launch {
            val donor = DonorEntity(
                name = name.trim(),
                bloodGroup = bloodGroup,
                phone = phone.trim(),
                district = district.trim(),
                area = area.trim(),
                totalDonations = totalDonations,
                lastDonationDate = lastDonationDate.ifEmpty { "তথ্য নেই" },
                lastDonationTimestamp = System.currentTimeMillis(),
                isAvailable = true,
                notes = notes.trim()
            )
            val newId = repository.insertDonor(donor)
            if (totalDonations > 0 && lastDonationDate.isNotEmpty()) {
                repository.insertDonationRecord(
                    DonationRecordEntity(
                        donorId = newId,
                        donorName = name.trim(),
                        bloodGroup = bloodGroup,
                        donationDate = lastDonationDate,
                        timestamp = System.currentTimeMillis(),
                        hospital = if (area.isNotEmpty()) "$area, $district" else district,
                        recipientInfo = "পূর্ববর্তী রক্তদান বিবরণ",
                        bags = 1,
                        note = "নিবন্ধনকালীন সংরক্ষিত রেকর্ড"
                    )
                )
            }
            _showAddDonorDialog.value = false
        }
    }

    fun logNewDonation(
        donor: DonorEntity,
        hospital: String,
        recipientInfo: String,
        bags: Int,
        note: String,
        date: String
    ) {
        viewModelScope.launch {
            val formattedDate = date.ifEmpty {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale("bn", "BD"))
                sdf.format(Date())
            }
            val record = DonationRecordEntity(
                donorId = donor.id,
                donorName = donor.name,
                bloodGroup = donor.bloodGroup,
                donationDate = formattedDate,
                timestamp = System.currentTimeMillis(),
                hospital = hospital.trim(),
                recipientInfo = recipientInfo.trim(),
                bags = bags,
                note = note.trim()
            )
            repository.insertDonationRecord(record)
            _donorForLogging.value = null
        }
    }

    fun createEmergencyRequest(
        context: Context,
        patientName: String,
        bloodGroup: String,
        hospital: String,
        district: String,
        bagsNeeded: Int,
        urgencyLevel: String,
        contactPhone: String,
        deadline: String,
        notes: String,
        sendPushAlert: Boolean
    ) {
        viewModelScope.launch {
            val request = EmergencyRequestEntity(
                patientName = patientName.trim(),
                bloodGroup = bloodGroup,
                hospital = hospital.trim(),
                district = district.trim(),
                bagsNeeded = bagsNeeded,
                urgencyLevel = urgencyLevel,
                contactPhone = contactPhone.trim(),
                deadline = deadline.trim().ifEmpty { "যত দ্রুত সম্ভব" },
                timestamp = System.currentTimeMillis(),
                status = "রক্ত খুঁজছে",
                notes = notes.trim()
            )
            val insertedId = repository.insertEmergencyRequest(request)
            _showEmergencyDialog.value = false

            if (sendPushAlert) {
                val savedRequest = request.copy(id = insertedId)
                EmergencyNotificationHelper.showEmergencyBloodNotification(context, savedRequest)
            }
        }
    }

    fun updateEmergencyStatus(id: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateRequestStatus(id, newStatus)
        }
    }

    fun testEmergencyNotification(context: Context, request: EmergencyRequestEntity) {
        EmergencyNotificationHelper.showEmergencyBloodNotification(context, request)
    }

    fun findCompatibleDonorsForRequest(bloodGroup: String) {
        val type = BloodType.fromLabel(bloodGroup)
        if (type != null) {
            // Find who can donate to this recipient
            val compatibleTypes = BloodType.canReceiveFrom(type).map { it.label }
            // For now, filter by exact or compatible
            _selectedBloodFilter.value = bloodGroup
            _selectedTab.value = 0 // Switch to Donors tab
        } else {
            _selectedBloodFilter.value = bloodGroup
            _selectedTab.value = 0
        }
    }
}

class BloodViewModelFactory(
    private val repository: BloodDonationRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BloodViewModel::class.java)) {
            return BloodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
