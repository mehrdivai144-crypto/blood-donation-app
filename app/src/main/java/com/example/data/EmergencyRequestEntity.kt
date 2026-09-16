package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_requests")
data class EmergencyRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val bloodGroup: String, // e.g. "O+", "AB-"
    val hospital: String,
    val district: String = "ঢাকা",
    val bagsNeeded: Int = 1,
    val urgencyLevel: String = "জরুরি", // "জরুরি", "অতি জরুরি / লাইফ সাপোর্ট", "পরিকল্পিত অপারেশন"
    val contactPhone: String,
    val deadline: String = "যত দ্রুত সম্ভব",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "রক্ত খুঁজছে", // "রক্ত খুঁজছে", "দাতা পাওয়া গেছে", "সম্পন্ন"
    val notes: String = ""
)
