package com.example.data

/**
 * Standard 8 Blood Types with compatibility mapping and clinical rules.
 */
enum class BloodType(val label: String, val bengaliLabel: String) {
    A_POSITIVE("A+", "এ পজিটিভ (A+)"),
    A_NEGATIVE("A-", "এ নেগেটিভ (A-)"),
    B_POSITIVE("B+", "বি পজিটিভ (B+)"),
    B_NEGATIVE("B-", "বি নেগেটিভ (B-)"),
    AB_POSITIVE("AB+", "এবি পজিটিভ (AB+)"),
    AB_NEGATIVE("AB-", "এবি নেগেটিভ (AB-)"),
    O_POSITIVE("O+", "ও পজিটিভ (O+)"),
    O_NEGATIVE("O-", "ও নেগেটিভ (O-)");

    companion object {
        fun fromLabel(label: String): BloodType? {
            val clean = label.trim().uppercase()
            return entries.firstOrNull { it.label == clean }
        }

        val allLabels = entries.map { it.label }

        /**
         * Checks if a donor can donate Red Blood Cells to a recipient.
         */
        fun isCompatible(donor: BloodType, recipient: BloodType): Boolean {
            return recipient in canDonateTo(donor)
        }

        /**
         * List of blood groups this donor can donate red cells to.
         */
        fun canDonateTo(donor: BloodType): List<BloodType> {
            return when (donor) {
                O_NEGATIVE -> listOf(O_NEGATIVE, O_POSITIVE, A_NEGATIVE, A_POSITIVE, B_NEGATIVE, B_POSITIVE, AB_NEGATIVE, AB_POSITIVE)
                O_POSITIVE -> listOf(O_POSITIVE, A_POSITIVE, B_POSITIVE, AB_POSITIVE)
                A_NEGATIVE -> listOf(A_NEGATIVE, A_POSITIVE, AB_NEGATIVE, AB_POSITIVE)
                A_POSITIVE -> listOf(A_POSITIVE, AB_POSITIVE)
                B_NEGATIVE -> listOf(B_NEGATIVE, B_POSITIVE, AB_NEGATIVE, AB_POSITIVE)
                B_POSITIVE -> listOf(B_POSITIVE, AB_POSITIVE)
                AB_NEGATIVE -> listOf(AB_NEGATIVE, AB_POSITIVE)
                AB_POSITIVE -> listOf(AB_POSITIVE)
            }
        }

        /**
         * List of blood groups this recipient can receive red cells from.
         */
        fun canReceiveFrom(recipient: BloodType): List<BloodType> {
            return when (recipient) {
                O_NEGATIVE -> listOf(O_NEGATIVE)
                O_POSITIVE -> listOf(O_POSITIVE, O_NEGATIVE)
                A_NEGATIVE -> listOf(A_NEGATIVE, O_NEGATIVE)
                A_POSITIVE -> listOf(A_POSITIVE, A_NEGATIVE, O_POSITIVE, O_NEGATIVE)
                B_NEGATIVE -> listOf(B_NEGATIVE, O_NEGATIVE)
                B_POSITIVE -> listOf(B_POSITIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE)
                AB_NEGATIVE -> listOf(AB_NEGATIVE, A_NEGATIVE, B_NEGATIVE, O_NEGATIVE)
                AB_POSITIVE -> listOf(AB_POSITIVE, AB_NEGATIVE, A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, O_POSITIVE, O_NEGATIVE)
            }
        }

        /**
         * Clinical characteristics & description in Bengali
         */
        fun getCharacteristicsBengali(type: BloodType): BloodInfo {
            return when (type) {
                O_NEGATIVE -> BloodInfo(
                    title = "সার্বজনীন লোহিত রক্তদাতা (Universal Red Cell Donor)",
                    description = "O- গ্রুপের রক্ত যেকোনো গ্রুপের রোগীকে জরুরি মুহূর্তে দেওয়া যায়। তবে O- ব্যক্তিরা কেবল O- রক্তই গ্রহণ করতে পারেন।",
                    rarity = "বিরল (৭-৮%)",
                    badge = "সার্বজনীন দাতা"
                )
                O_POSITIVE -> BloodInfo(
                    title = "সবচেয়ে চাহিদাসম্পন্ন রক্ত",
                    description = "O+ যেকোনো পজিটিভ গ্রুপকে (O+, A+, B+, AB+) রক্ত দিতে পারে। এটি প্রায় ৩৮% মানুষের রক্তের গ্রুপ।",
                    rarity = "খুবই সাধারণ (৩৮%)",
                    badge = "উচ্চ চাহিদা"
                )
                A_POSITIVE -> BloodInfo(
                    title = "জনপ্রিয় পজিটিভ গ্রুপ",
                    description = "A+ রক্ত A+ এবং AB+ রোগীদের দেওয়া যায়। এটি A+, A-, O+, O- থেকে রক্ত নিতে পারে।",
                    rarity = "সাধারণ (৩৪%)",
                    badge = "সাধারণ"
                )
                A_NEGATIVE -> BloodInfo(
                    title = "গুরুত্বপূর্ণ নেগেটিভ গ্রুপ",
                    description = "A- ব্যক্তিরা A+, A-, AB+, AB- কে রক্ত দিতে পারেন। তারা শুধুমাত্র A- এবং O- থেকে রক্ত নিতে পারেন।",
                    rarity = "বিরল (৬%)",
                    badge = "নেগেটিভ"
                )
                B_POSITIVE -> BloodInfo(
                    title = "উচ্চ চাহিদাসম্পন্ন রক্ত",
                    description = "B+ রক্ত B+ এবং AB+ কে দেওয়া যায়। এটি B+, B-, O+, O- থেকে গ্রহণ করতে পারে।",
                    rarity = "সাধারণ (৯-১০%)",
                    badge = "সাধারণ"
                )
                B_NEGATIVE -> BloodInfo(
                    title = "বিরল রক্তের গ্রুপ",
                    description = "B- রক্ত B+, B-, AB+, AB- কে দেওয়া যায়। কেবল B- এবং O- রক্ত নিতে পারে।",
                    rarity = "খুবই বিরল (১.৫-২%)",
                    badge = "বিরল"
                )
                AB_POSITIVE -> BloodInfo(
                    title = "সার্বজনীন লোহিত রক্তগ্রহীতা (Universal Recipient)",
                    description = "AB+ ব্যক্তিরা যেকোনো গ্রুপের রক্ত নিরাপদে গ্রহণ করতে পারেন। তবে তারা রক্ত দিতে পারেন কেবল AB+ রোগীকে।",
                    rarity = "কম সাধারণ (৩-৪%)",
                    badge = "সার্বজনীন গ্রহীতা"
                )
                AB_NEGATIVE -> BloodInfo(
                    title = "সবচেয়ে বিরল রক্তের গ্রুপ",
                    description = "AB- ব্যক্তিরা AB- এবং AB+ কে রক্ত দিতে পারেন। তারা সকল নেগেটিভ রক্তের গ্রুপ থেকে রক্ত গ্রহণ করতে পারেন।",
                    rarity = "অত্যন্ত বিরল (১% এর নিচে)",
                    badge = "অতি বিরল"
                )
            }
        }
    }
}

data class BloodInfo(
    val title: String,
    val description: String,
    val rarity: String,
    val badge: String
)
