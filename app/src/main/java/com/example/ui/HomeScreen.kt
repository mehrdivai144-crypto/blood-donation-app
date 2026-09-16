package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BloodType
import com.example.data.DonationRecordEntity
import com.example.data.DonorEntity
import com.example.data.EmergencyRequestEntity
import com.example.notification.EmergencyNotificationHelper
import com.example.ui.components.AddDonationLogDialog
import com.example.ui.components.AddDonorDialog
import com.example.ui.components.AddEmergencyRequestDialog
import com.example.ui.components.BloodCompatibilityView
import com.example.ui.components.DonorCard
import com.example.ui.components.DonorHistoryDialog
import com.example.ui.components.EmergencyRequestCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BloodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Request notification permission for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "জরুরি নোটিফিকেশন সক্রিয় হয়েছে!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val donors by viewModel.donors.collectAsStateWithLifecycle()
    val emergencyRequests by viewModel.emergencyRequests.collectAsStateWithLifecycle()
    val activeEmergencyCount by viewModel.activeEmergencyCount.collectAsStateWithLifecycle()
    val donationRecords by viewModel.donationRecords.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedBloodFilter by viewModel.selectedBloodFilter.collectAsStateWithLifecycle()
    val onlyAvailableFilter by viewModel.onlyAvailableFilter.collectAsStateWithLifecycle()

    val compatibilitySelectedGroup by viewModel.compatibilitySelectedGroup.collectAsStateWithLifecycle()
    val testDonorGroup by viewModel.testDonorGroup.collectAsStateWithLifecycle()
    val testRecipientGroup by viewModel.testRecipientGroup.collectAsStateWithLifecycle()

    val showAddDonorDialog by viewModel.showAddDonorDialog.collectAsStateWithLifecycle()
    val donorForLogging by viewModel.donorForLogging.collectAsStateWithLifecycle()
    val donorForDetails by viewModel.donorForDetails.collectAsStateWithLifecycle()
    val showEmergencyDialog by viewModel.showEmergencyDialog.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "রক্তদান",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "জীবন বাঁচাতে রক্ত দিন • রক্তদাতা অনুসন্ধান",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // Quick Action: Send/Test Alert Notification
                    IconButton(
                        onClick = {
                            val sample = emergencyRequests.firstOrNull { it.status == "রক্ত খুঁজছে" }
                                ?: EmergencyRequestEntity(
                                    patientName = "জরুরি রোগী",
                                    bloodGroup = "O-",
                                    hospital = "ঢাকা মেডিকেল কলেজ",
                                    district = "ঢাকা",
                                    bagsNeeded = 1,
                                    urgencyLevel = "অতি জরুরি",
                                    contactPhone = "01711000111"
                                )
                            EmergencyNotificationHelper.showEmergencyBloodNotification(context, sample)
                            Toast.makeText(context, "জরুরি নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("appbar_notification_test_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "জরুরি নোটিফিকেশন টেস্ট",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                // Tab 0: Donors & Search
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = {
                        Icon(imageVector = Icons.Default.Group, contentDescription = "রক্তদাতা")
                    },
                    label = { Text("রক্তদাতা", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_donors")
                )

                // Tab 1: Emergency Requests
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = {
                        if (activeEmergencyCount > 0) {
                            BadgedBox(badge = {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text("$activeEmergencyCount")
                                }
                            }) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = "জরুরি চাহিদা")
                            }
                        } else {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = "জরুরি চাহিদা")
                        }
                    },
                    label = { Text("জরুরি চাহিদা", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_emergency")
                )

                // Tab 2: Compatibility Checker
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = {
                        Icon(imageVector = Icons.Default.Bloodtype, contentDescription = "ম্যাচিং")
                    },
                    label = { Text("ম্যাচিং যাচাই", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_compatibility")
                )

                // Tab 3: History & Stats
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = {
                        Icon(imageVector = Icons.Default.History, contentDescription = "ইতিহাস")
                    },
                    label = { Text("ইতিহাস", fontSize = 11.sp) },
                    modifier = Modifier.testTag("tab_history")
                )
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openAddDonorDialog() },
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                        text = { Text("নতুন দাতা নিবন্ধন") },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("fab_add_donor")
                    )
                }
                1 -> {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openEmergencyDialog() },
                        icon = { Icon(Icons.Default.Add, contentDescription = null) },
                        text = { Text("জরুরি রক্তের আবেদন") },
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                        modifier = Modifier.testTag("fab_add_emergency")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DonorsTabContent(
                    donors = donors,
                    searchQuery = searchQuery,
                    selectedBloodFilter = selectedBloodFilter,
                    onlyAvailable = onlyAvailableFilter,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    onSelectBloodFilter = { viewModel.setBloodFilter(it) },
                    onToggleAvailable = { viewModel.toggleOnlyAvailable(it) },
                    onLogDonation = { donor -> viewModel.openLogDonationDialog(donor) },
                    onViewHistory = { donor -> viewModel.openDonorDetails(donor) },
                    onAddDonorClick = { viewModel.openAddDonorDialog() }
                )

                1 -> EmergencyTabContent(
                    requests = emergencyRequests,
                    onAddRequest = { viewModel.openEmergencyDialog() },
                    onFindDonors = { bloodGroup -> viewModel.findCompatibleDonorsForRequest(bloodGroup) },
                    onSendNotification = { request ->
                        viewModel.testEmergencyNotification(context, request)
                        Toast.makeText(context, "${request.bloodGroup} এর জরুরি নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                    },
                    onToggleStatus = { id, newStatus ->
                        viewModel.updateEmergencyStatus(id, newStatus)
                    }
                )

                2 -> BloodCompatibilityView(
                    selectedType = compatibilitySelectedGroup,
                    donorType = testDonorGroup,
                    recipientType = testRecipientGroup,
                    onSelectType = { viewModel.setCompatibilitySelectedGroup(it) },
                    onSelectDonorType = { viewModel.setTestDonorGroup(it) },
                    onSelectRecipientType = { viewModel.setTestRecipientGroup(it) },
                    onFindDonorsForGroup = { group ->
                        viewModel.findCompatibleDonorsForRequest(group)
                    }
                )

                3 -> HistoryTabContent(
                    donorsCount = donors.size,
                    donationRecords = donationRecords,
                    emergencyRequests = emergencyRequests,
                    onSelectDonorForHistory = { donor -> viewModel.openDonorDetails(donor) },
                    donorsList = donors
                )
            }
        }
    }

    // Dialogs
    if (showAddDonorDialog) {
        AddDonorDialog(
            onDismiss = { viewModel.closeAddDonorDialog() },
            onRegister = { name, group, phone, district, area, donations, date, notes ->
                viewModel.registerDonor(name, group, phone, district, area, donations, date, notes)
                Toast.makeText(context, "নতুন রক্তদাতা সফলভাবে নিবন্ধিত হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    donorForLogging?.let { donor ->
        AddDonationLogDialog(
            donor = donor,
            onDismiss = { viewModel.closeLogDonationDialog() },
            onSaveLog = { hospital, recipient, bags, note, date ->
                viewModel.logNewDonation(donor, hospital, recipient, bags, note, date)
                Toast.makeText(context, "রক্তদানের রেকর্ড যুক্ত হয়েছে ও সংখ্যা হালনাগাদ হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    donorForDetails?.let { donor ->
        DonorHistoryDialog(
            donor = donor,
            historyList = donationRecords,
            onDismiss = { viewModel.closeDonorDetails() },
            onLogNewDonation = {
                viewModel.closeDonorDetails()
                viewModel.openLogDonationDialog(donor)
            }
        )
    }

    if (showEmergencyDialog) {
        AddEmergencyRequestDialog(
            onDismiss = { viewModel.closeEmergencyDialog() },
            onSubmitRequest = { patient, group, hospital, district, bags, urgency, phone, deadline, notes, sendPush ->
                viewModel.createEmergencyRequest(
                    context, patient, group, hospital, district, bags, urgency, phone, deadline, notes, sendPush
                )
                Toast.makeText(context, "জরুরি আবেদন পোস্ট করা হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/**
 * Tab 0: Donors Search & Directory
 */
@Composable
private fun DonorsTabContent(
    donors: List<DonorEntity>,
    searchQuery: String,
    selectedBloodFilter: String,
    onlyAvailable: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectBloodFilter: (String) -> Unit,
    onToggleAvailable: (Boolean) -> Unit,
    onLogDonation: (DonorEntity) -> Unit,
    onViewHistory: (DonorEntity) -> Unit,
    onAddDonorClick: () -> Unit
) {
    val filterGroups = listOf("সব", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("নাম, জেলা, এলাকা বা ফোন নম্বর খুঁজুন...") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "মুছুন")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_donors_input"),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Blood Group filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            filterGroups.forEach { group ->
                val isSelected = group == selectedBloodFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectBloodFilter(group) },
                    label = {
                        Text(
                            text = group,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("filter_chip_$group")
                )
            }

            // Only Available Toggle Chip
            FilterChip(
                selected = onlyAvailable,
                onClick = { onToggleAvailable(!onlyAvailable) },
                label = { Text("শুধুমাত্র প্রস্তুত") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Donors count bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${donors.size} জন রক্তদাতা পাওয়া গেছে",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            if (selectedBloodFilter != "সব") {
                Text(
                    text = "ফিল্টার: $selectedBloodFilter",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (donors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "কোন রক্তদাতা পাওয়া যায়নি",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "অন্য রক্তের গ্রুপ বা জেলা দিয়ে সন্ধান করুন।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(onClick = onAddDonorClick) {
                        Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("নতুন দাতা যোগ করুন")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(donors, key = { it.id }) { donor ->
                    DonorCard(
                        donor = donor,
                        onLogDonation = { onLogDonation(donor) },
                        onViewHistory = { onViewHistory(donor) }
                    )
                }
            }
        }
    }
}

/**
 * Tab 1: Emergency Requests & Urgent Notifications
 */
@Composable
private fun EmergencyTabContent(
    requests: List<EmergencyRequestEntity>,
    onAddRequest: () -> Unit,
    onFindDonors: (String) -> Unit,
    onSendNotification: (EmergencyRequestEntity) -> Unit,
    onToggleStatus: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Emergency Hero Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "জরুরি রক্তের নোটিফিকেশন কেন্দ্র",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = "বর্তমানে রক্ত খুঁজছেন এমন রোগীদের জরুরি তথ্য। যেকোনো আবেদনের ঘণ্টা আইকনে চাপলে ইনস্ট্যান্ট ডিভাইস নোটিফিকেশন পাওয়া যাবে।",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "চলমান রক্তের আবেদনসমূহ (${requests.size} টি)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            FilledTonalButton(
                onClick = onAddRequest,
                modifier = Modifier.testTag("emergency_banner_add_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("নতুন আবেদন", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (requests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalHospital,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "বর্তমানে কোনো জরুরি রক্তের আবেদন নেই",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(requests, key = { it.id }) { request ->
                    EmergencyRequestCard(
                        request = request,
                        onFindDonors = onFindDonors,
                        onSendNotification = { onSendNotification(request) },
                        onToggleStatus = { nextStatus -> onToggleStatus(request.id, nextStatus) }
                    )
                }
            }
        }
    }
}

/**
 * Tab 3: History & Community Impact
 */
@Composable
private fun HistoryTabContent(
    donorsCount: Int,
    donationRecords: List<DonationRecordEntity>,
    emergencyRequests: List<EmergencyRequestEntity>,
    onSelectDonorForHistory: (DonorEntity) -> Unit,
    donorsList: List<DonorEntity>
) {
    val totalBags = donationRecords.sumOf { it.bags }
    val totalDonationsFromDonors = donorsList.sumOf { it.totalDonations }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Impact Stats Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "মোট রক্তদান",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "$totalDonationsFromDonors বার",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "নিবন্ধিত দাতা",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "$donorsCount জন",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "সংরক্ষিত ব্যাগ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "$totalBags ব্যাগ",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "রক্তদানের সাম্প্রতিক ইতিহাস ও টাইমলাইন",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (donationRecords.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "কোন রক্তদানের ইতিহাস এখনো এন্ট্রি করা হয়নি।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(donationRecords, key = { it.id }) { record ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = record.bloodGroup,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontSize = 15.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = record.donorName,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = record.donationDate,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }

                                Spacer(modifier = Modifier.height(3.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocalHospital,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = record.hospital,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                if (record.recipientInfo.isNotEmpty()) {
                                    Text(
                                        text = "গ্রহীতা: ${record.recipientInfo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
