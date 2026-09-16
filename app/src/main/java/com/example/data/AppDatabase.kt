package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        DonorEntity::class,
        DonationRecordEntity::class,
        EmergencyRequestEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun donorDao(): DonorDao
    abstract fun donationRecordDao(): DonationRecordDao
    abstract fun emergencyRequestDao(): EmergencyRequestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "roktodaan_db"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed database in background thread
                        CoroutineScope(Dispatchers.IO).launch {
                            seedInitialData(getInstance(context))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: AppDatabase) {
            val donorDao = db.donorDao()
            val recordDao = db.donationRecordDao()
            val requestDao = db.emergencyRequestDao()

            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000

            // Seed initial Donors
            val donors = listOf(
                DonorEntity(
                    id = 1,
                    name = "তানভীর আহমেদ",
                    bloodGroup = "O+",
                    phone = "01711000111",
                    district = "ঢাকা",
                    area = "মিরপুর-২",
                    totalDonations = 14,
                    lastDonationDate = "১০ জানুয়ারি ২০২৬",
                    lastDonationTimestamp = now - (65 * dayMillis),
                    isAvailable = true,
                    gender = "পুরুষ",
                    notes = "নিয়মিত রক্তদাতা, যেকোনো জরুরি সময়ে যোগাযোগ করুন।"
                ),
                DonorEntity(
                    id = 2,
                    name = "ফাতিমা আক্তার",
                    bloodGroup = "A+",
                    phone = "01819222333",
                    district = "ঢাকা",
                    area = "ধানমন্ডি",
                    totalDonations = 8,
                    lastDonationDate = "২৫ অক্টোবর ২০২৫",
                    lastDonationTimestamp = now - (140 * dayMillis),
                    isAvailable = true,
                    gender = "নারী",
                    notes = "থ্যালাসেমিয়া শিশুদের জন্য নিয়মিত রক্ত দেন।"
                ),
                DonorEntity(
                    id = 3,
                    name = "রাকিবুল হাসান",
                    bloodGroup = "B+",
                    phone = "01912333444",
                    district = "চট্টগ্রাম",
                    area = "জিইসি মোড়",
                    totalDonations = 11,
                    lastDonationDate = "১৫ ডিসেম্বর ২০২৫",
                    lastDonationTimestamp = now - (92 * dayMillis),
                    isAvailable = true,
                    gender = "পুরুষ",
                    notes = "স্বেচ্ছাসেবী রক্তদান ক্লাবের সংগঠক।"
                ),
                DonorEntity(
                    id = 4,
                    name = "ড. সাজিদ মাহমুদ",
                    bloodGroup = "O-",
                    phone = "01755444555",
                    district = "ঢাকা",
                    area = "শাহবাগ",
                    totalDonations = 18,
                    lastDonationDate = "০৫ নভেম্বর ২০২৫",
                    lastDonationTimestamp = now - (130 * dayMillis),
                    isAvailable = true,
                    gender = "পুরুষ",
                    notes = "সার্বজনীন দাতা (O Negative) - জরুরি অস্ত্রোপচারে সাহায্য করেন।"
                ),
                DonorEntity(
                    id = 5,
                    name = "নুসরাত জাহান",
                    bloodGroup = "AB+",
                    phone = "01677666777",
                    district = "রাজশাহী",
                    area = "মতিহার",
                    totalDonations = 5,
                    lastDonationDate = "২০ জানুয়ারি ২০২৬",
                    lastDonationTimestamp = now - (55 * dayMillis),
                    isAvailable = false, // Resting period
                    gender = "নারী",
                    notes = "সম্প্রতি রক্তদান করেছেন, বিশ্রামে আছেন।"
                ),
                DonorEntity(
                    id = 6,
                    name = "মেহেদী হাসান শুভ",
                    bloodGroup = "B-",
                    phone = "01521888999",
                    district = "সিলেট",
                    area = "জিন্দাবাজার",
                    totalDonations = 9,
                    lastDonationDate = "১৮ আগস্ট ২০২৫",
                    lastDonationTimestamp = now - (210 * dayMillis),
                    isAvailable = true,
                    gender = "পুরুষ",
                    notes = "বিরল বি নেগেটিভ রক্তের অধিকারী।"
                ),
                DonorEntity(
                    id = 7,
                    name = "সায়েম ইসলাম",
                    bloodGroup = "A-",
                    phone = "01799000888",
                    district = "খুলনা",
                    area = "খালিশপুর",
                    totalDonations = 6,
                    lastDonationDate = "১২ সেপ্টেম্বর ২০২৫",
                    lastDonationTimestamp = now - (185 * dayMillis),
                    isAvailable = true,
                    gender = "পুরুষ",
                    notes = "জরুরি নেগেটিভ গ্রুপের প্রয়োজনে ডাকলেই পাওয়া যায়।"
                ),
                DonorEntity(
                    id = 8,
                    name = "আফরোজা খানম",
                    bloodGroup = "AB-",
                    phone = "01300111222",
                    district = "ঢাকা",
                    area = "উত্তরা",
                    totalDonations = 4,
                    lastDonationDate = "০২ অক্টোবর ২০২৫",
                    lastDonationTimestamp = now - (165 * dayMillis),
                    isAvailable = true,
                    gender = "নারী",
                    notes = "অতি বিরল এবি নেগেটিভ রক্তদাতা।"
                )
            )
            donorDao.insertDonors(donors)

            // Seed Donation History Records
            val records = listOf(
                DonationRecordEntity(
                    donorId = 1,
                    donorName = "তানভীর আহমেদ",
                    bloodGroup = "O+",
                    donationDate = "১০ জানুয়ারি ২০২৬",
                    timestamp = now - (65 * dayMillis),
                    hospital = "ঢাকা মেডিকেল কলেজ হাসপাতাল",
                    recipientInfo = "দুর্ঘটনায় আহত রোগী",
                    bags = 1,
                    note = "জরুরি অস্ত্রোপচারের জন্য ১ ব্যাগ রক্ত প্রদান।"
                ),
                DonationRecordEntity(
                    donorId = 4,
                    donorName = "ড. সাজিদ মাহমুদ",
                    bloodGroup = "O-",
                    donationDate = "০৫ নভেম্বর ২০২৫",
                    timestamp = now - (130 * dayMillis),
                    hospital = "বঙ্গবন্ধু শেখ মুজিব মেডিকেল বিশ্ববিদ্যালয় (BSMMU)",
                    recipientInfo = "আইসিইউতে থাকা নবজাতক শিশু",
                    bags = 1,
                    note = "জরুরি অবস্থায় সার্বজনীন ও নেগেটিভ রক্ত প্রদান।"
                ),
                DonationRecordEntity(
                    donorId = 3,
                    donorName = "রাকিবুল হাসান",
                    bloodGroup = "B+",
                    donationDate = "১৫ ডিসেম্বর ২০২৫",
                    timestamp = now - (92 * dayMillis),
                    hospital = "চট্টগ্রাম মেডিকেল কলেজ হাসপাতাল",
                    recipientInfo = "থ্যালাসেমিয়া রোগী আসিফ (১০ বছর)",
                    bags = 1,
                    note = "নিয়মিত রক্তদান কর্মসূচির অংশ।"
                ),
                DonationRecordEntity(
                    donorId = 2,
                    donorName = "ফাতিমা আক্তার",
                    bloodGroup = "A+",
                    donationDate = "২৫ অক্টোবর ২০২৫",
                    timestamp = now - (140 * dayMillis),
                    hospital = "বারডেম জেনারেল হাসপাতাল",
                    recipientInfo = "কিডনি ডায়ালাইসিস রোগী",
                    bags = 1,
                    note = "সফলভাবে ১ ব্যাগ এ পজিটিভ রক্ত দেওয়া হয়েছে।"
                )
            )
            recordDao.insertRecords(records)

            // Seed Emergency Blood Requests
            val requests = listOf(
                EmergencyRequestEntity(
                    patientName = "রাবেয়া বেগম (৫৫)",
                    bloodGroup = "B-",
                    hospital = "ঢাকা মেডিকেল কলেজ হাসপাতাল (জরুরি বিভাগ)",
                    district = "ঢাকা",
                    bagsNeeded = 2,
                    urgencyLevel = "অতি জরুরি / লাইফ সাপোর্ট",
                    contactPhone = "01712999888",
                    deadline = "আজ দুপুর ২টার মধ্যে",
                    timestamp = now - (2 * 60 * 60 * 1000), // 2 hours ago
                    status = "রক্ত খুঁজছে",
                    notes = "আইসিইউতে ভেন্টিলেশনে আছেন, দ্রুত ২ ব্যাগ বি নেগেটিভ রক্তের প্রয়োজন।"
                ),
                EmergencyRequestEntity(
                    patientName = "হাসান মাহমুদ (৩২)",
                    bloodGroup = "O+",
                    hospital = "কুর্মিটোলা জেনারেল হাসপাতাল",
                    district = "ঢাকা",
                    bagsNeeded = 1,
                    urgencyLevel = "জরুরি",
                    contactPhone = "01822334455",
                    deadline = "আজ সন্ধ্যার পূর্বে",
                    timestamp = now - (4 * 60 * 60 * 1000),
                    status = "রক্ত খুঁজছে",
                    notes = "সড়ক দুর্ঘটনায় প্রচুর রক্তক্ষরণ হয়েছে। ও পজিটিভ দাতা প্রয়োজন।"
                ),
                EmergencyRequestEntity(
                    patientName = "আরিয়ান আহমেদ (৭)",
                    bloodGroup = "A+",
                    hospital = "চট্টগ্রাম মা ও শিশু হাসপাতাল",
                    district = "চট্টগ্রাম",
                    bagsNeeded = 1,
                    urgencyLevel = "জরুরি",
                    contactPhone = "01988776655",
                    deadline = "আগামীকাল সকাল ১০টা",
                    timestamp = now - (6 * 60 * 60 * 1000),
                    status = "রক্ত খুঁজছে",
                    notes = "থ্যালাসেমিয়া আক্রান্ত শিশুর নিয়মিত রক্ত সঞ্চালন।"
                ),
                EmergencyRequestEntity(
                    patientName = "শামীমা নাসরিন (২৮)",
                    bloodGroup = "AB-",
                    hospital = "সিলেট ওসমানী মেডিকেল কলেজ",
                    district = "সিলেট",
                    bagsNeeded = 2,
                    urgencyLevel = "অতি জরুরি / লাইফ সাপোর্ট",
                    contactPhone = "01733445566",
                    deadline = "যত দ্রুত সম্ভব",
                    timestamp = now - (10 * 60 * 60 * 1000),
                    status = "রক্ত খুঁজছে",
                    notes = "সিজারিয়ান জটিলতার কারণে এবি নেগেটিভ রক্তের জরুরি ডাক।"
                )
            )
            requestDao.insertRequests(requests)
        }
    }
}
