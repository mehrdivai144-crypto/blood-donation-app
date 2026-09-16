package com.example.blooddonation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ডাটা মডেল
data class Donor(
    val id: String,
    val name: String,
    val bloodGroup: String,
    val location: String,
    val phone: String,
    val totalDonations: Int,
    val lastDonationDate: String,
    val isReady: Boolean = true
)

data class EmergencyRequest(
    val id: String,
    val patientName: String,
    val bloodGroup: String,
    val hospital: String,
    val location: String,
    val contactPhone: String,
    val timeLimit: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationApp() {
    var currentTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGroupFilter by remember { mutableStateOf("সব") }
    var showRegisterDialog by remember { mutableStateOf(false) }

    // নমুনা ডাটা
    val donorsList = remember {
        mutableStateListOf(
            Donor("1", "ডা. সাজিদ মাহমুদ", "O-", "ধানমন্ডি, ঢাকা", "01711000000", 18, "০৫ নভেম্বর ২০২৫"),
            Donor("2", "তানভীর আহমেদ", "O+", "মিরপুর-২, ঢাকা", "01811000000", 14, "১০ জানুয়ারি ২০২৬"),
            Donor("3", "রাকিবুল হাসান", "B+", "জিইসি মোড়, চট্টগ্রাম", "01911000000", 11, "১৫ ডিসেম্বর ২০२५"),
            Donor("4", "সাইফুল ইসলাম", "A+", "উত্তরা, ঢাকা", "01611000000", 7, "२० ফেব্রুয়ারি २०२६")
        )
    }

    val emergencyRequests = remember {
        mutableStateListOf(
            EmergencyRequest("1", "আব্দুর রহিম", "O-", "স্কয়ার হাসপাতাল", "পান্থপথ, ঢাকা", "01700112233", "জরুরি (२ ঘণ्टा)"),
            EmergencyRequest("2", "নাসরিন আক्তার", "A+", "চট्টগ्রাম মেডিকেল", "চকবাজার, চট्टগ्রাম", "01800112233", "আজ সন্ধ्যার मধ्ये")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("রক্তদান - জীবন বাঁচান", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFB71C1C)),
                actions = {
                    IconButton(onClick = { }) {
                        BadgedBox(
                            badge = { Badge { Text("${emergencyRequests.size}") } }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Alerts", tint = Color.White)
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showRegisterDialog = true },
                containerColor = Color(0xFFB71C1C),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Add") },
                text = { Text("নতুন দাতা নিবন্ধন") }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF212121), contentColor = Color.White) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Donors") },
                    label = { Text("রক্তদাতা") }
                )
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Emergency") },
                    label = { Text("জরুরি চাহিদা") }
                )
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.CompareArrows, contentDescription = "Compatibility") },
                    label = { Text("ম্যাচিং যাচাই") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF121212))
        ) {
            when (currentTab) {
                0 -> DonorsScreen(
                    donorsList = donorsList,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    selectedGroup = selectedGroupFilter,
                    onGroupSelect = { selectedGroupFilter = it }
                )
                1 -> EmergencyScreen(emergencyRequests)
                2 -> BloodCompatibilityScreen()
            }
        }

        if (showRegisterDialog) {
            RegisterDonorDialog(
                onDismiss = { showRegisterDialog = false },
                onRegister = { newDonor ->
                    donorsList.add(0, newDonor)
                    showRegisterDialog = false
                }
            )
        }
    }
}

// ১. রক্তদাতা ও ইতিহাস স্ক্রিন
@Composable
fun DonorsScreen(
    donorsList: List<Donor>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedGroup: String,
    onGroupSelect: (String) -> Unit
) {
    val bloodGroups = listOf("সব", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("নাম, জেলা বা এলাকা দিয়ে খুঁজুন...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFE53935),
                unfocusedBorderColor = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bloodGroups) { group ->
                FilterChip(
                    selected = selectedGroup == group,
                    onClick = { onGroupSelect(group) },
                    label = { Text(group) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFB71C1C),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF2A2A2A),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val filteredDonors = donorsList.filter { donor ->
            (selectedGroup == "সব" || donor.bloodGroup == selectedGroup) &&
                    (donor.name.contains(searchQuery, ignoreCase = true) ||
                            donor.location.contains(searchQuery, ignoreCase = true))
        }

        Text(
            text = "${filteredDonors.size} জন রক্তদাতা পাওয়া গেছে",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredDonors) { donor ->
                DonorCard(donor)
            }
        }
    }
}

@Composable
fun DonorCard(donor: Donor) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color(0xFFB71C1C), shape = RoundedCornerShape(12.dp))
                    ) {
                        Text(donor.bloodGroup, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(donor.location, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }
                Surface(
                    color = Color(0xFF2E7D32).copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "রক্তদানে প্রস্তুত",
                        color = Color(0xFF81C784),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.DarkGray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("রক্তদান: ${donor.totalDonations} বার", color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সর্বশেষ: ${donor.lastDonationDate}", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Call, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("যোগাযোগ করুন (${donor.phone})", color = Color.White)
            }
        }
    }
}

// २. जरुरी अधिसूचना और चाहिदा स्क्रीन
@Composable
fun EmergencyScreen(requests: List<EmergencyRequest>) {
    Column(modifier = Modifier.padding(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD32F2F).copy(alpha = 0.15f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("জরুরি রক্তের প্রয়োজন!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("আপনার রক্তের গ্রুপ মিলে গেলে দ্রুত যোগাযোগ করুন।", color = Color.LightGray, fontSize = 12.sp)
                }
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(requests) { req ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text("রোগী: ${req.patientName}", color = Color.White, fontWeight = FontWeight.Bold)
                            Text(req.timeLimit, color = Color(0xFFFFB74D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("রক্তের গ্রুপ: ${req.bloodGroup}", color = Color(0xFFE53935), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("হাসপাতাল: ${req.hospital}, ${req.location}", color = Color.LightGray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("রক্ত দিতে ইচ्ছুक (${req.contactPhone})")
                        }
                    }
                }
            }
        }
    }
}

// ३. रक्त की सामंजस्य (Blood Compatibility Checker)
@Composable
fun BloodCompatibilityScreen() {
    var selectedGroup by remember { mutableStateOf("O+") }
    val bloodGroups = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    val canGiveTo = when (selectedGroup) {
        "O-" -> listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        "O+" -> listOf("O+", "A+", "B+", "AB+")
        "A-" -> listOf("A+", "A-", "AB+", "AB-")
        "A+" -> listOf("A+", "AB+")
        "B-" -> listOf("B+", "B-", "AB+", "AB-")
        "B+" -> listOf("B+", "AB+")
        "AB-" -> listOf("AB+", "AB-")
        "AB+" -> listOf("AB+")
        else -> emptyList()
    }

    val canReceiveFrom = when (selectedGroup) {
        "O-" -> listOf("O-")
        "O+" -> listOf("O+", "O-")
        "A-" -> listOf("A-", "O-")
        "A+" -> listOf("A+", "A-", "O+", "O-")
        "B-" -> listOf("B-", "O-")
        "B+" -> listOf("B+", "B-", "O+", "O-")
        "AB-" -> listOf("AB-", "A-", "B-", "O-")
        "AB+" -> listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
        else -> emptyList()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("রক্তের গ্রুপ সামঞ্জস্য যাচাই", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("আপনার রক্তের গ্রুপ নির্বাচন করুন:", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(bloodGroups) { group ->
                FilterChip(
                    selected = selectedGroup == group,
                    onClick = { selectedGroup = group },
                    label = { Text(group, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFB71C1C),
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFF2A2A2A),
                        labelColor = Color.LightGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("কাকে রক্ত দিতে পারবেন:", color = Color(0xFF81C784), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(canGiveTo.joinToString(", "), color = Color.White, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.DarkGray)
                Spacer(modifier = Modifier.height(16.dp))

                Text("কার থেকে রক্ত নিতে পারবেন:", color = Color(0xFFFFB74D), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(canReceiveFrom.joinToString(", "), color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

// ४. नए दाता पंजीकरण की बातचीत
@Composable
fun RegisterDonorDialog(onDismiss: () -> Unit, onRegister: (Donor) -> Unit) {
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("O+") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("नए रक्तदाता पंजीकरण", fontWeight = FontWeight.Bold, color = Color.White) },
        containerColor = Color(0xFF212121),
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("पूर्ण नाम", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE53935),
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("क्षेत्र / जिला", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE53935),
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("मोबाइल नंबर", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE53935),
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("रक्त समूह (जैसे: A+, O-)", color = Color.Gray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFFE53935),
                        unfocusedBorderColor = Color.DarkGray
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty()) {
                        onRegister(
                            Donor(
                                id = System.currentTimeMillis().toString(),
                                name = name,
                                bloodGroup = bloodGroup,
                                location = location,
                                phone = phone,
                                totalDonations = 0,
                                lastDonationDate = "अभी रक्त नहीं दिया",
                                isReady = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))
            ) {
                Text("पंजीकरण करें", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = Color(0xFFE53935))
            }
        }
    )
}