package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "donation_records")
data class DonationRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val donorId: Long,
    val donorName: String,
    val bloodGroup: String,
    val donationDate: String, // e.g. "১২ ফেব্রুয়ারি ২০২৬"
    val timestamp: Long = System.currentTimeMillis(),
    val hospital: String, // e.g. "ঢাকা মেডিকেল কলেজ হাসপাতাল"
    val recipientInfo: String = "", // e.g. "থ্যালাসেমিয়া শিশু রোগী"
    val bags: Int = 1,
    val note: String = ""
)
