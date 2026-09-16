package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val bloodGroup: String, // e.g. "A+", "O-", etc.
    val phone: String,
    val district: String, // e.g. "ঢাকা", "চট্টগ্রাম"
    val area: String = "", // e.g. "মিরপুর-১০", "ধানমন্ডি"
    val totalDonations: Int = 0,
    val lastDonationDate: String = "", // e.g. "১৫ জানুয়ারি ২০২৬"
    val lastDonationTimestamp: Long = 0L,
    val isAvailable: Boolean = true,
    val gender: String = "পুরুষ",
    val notes: String = ""
)
