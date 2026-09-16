package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.BloodDonationRepository
import com.example.notification.EmergencyNotificationHelper
import com.example.ui.BloodViewModel
import com.example.ui.BloodViewModelFactory
import com.example.ui.HomeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create the notification channel for emergency blood requests
        EmergencyNotificationHelper.createNotificationChannel(this)

        val database = AppDatabase.getInstance(this)
        val repository = BloodDonationRepository(
            donorDao = database.donorDao(),
            recordDao = database.donationRecordDao(),
            emergencyRequestDao = database.emergencyRequestDao()
        )
        val viewModelFactory = BloodViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val bloodViewModel: BloodViewModel = viewModel(factory = viewModelFactory)
                    HomeScreen(viewModel = bloodViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "রক্তদান - $name",
        modifier = modifier
    )
}
