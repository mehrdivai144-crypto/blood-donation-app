# 🩸 Blood Donation App

A mobile application for managing blood donations and connecting donors with those in need. Built with **Kotlin** and **Jetpack Compose**.

## Features

✅ **Donor Management**
- Search donors by name, location, and blood type
- View donor contact information
- Track donation history

✅ **Emergency Requests**
- Real-time emergency blood requests
- Hospital location and contact details
- Notification badges for urgent needs

✅ **Blood Compatibility Checker**
- Interactive blood type compatibility
- See which blood types you can donate to/receive from
- Complete compatibility matrix

✅ **Donor Registration**
- Easy registration form for new donors
- Support for all blood types
- Bengali language support

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Material Design**: Material Design 3
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure

```
blood-donation-app/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/blooddonation/
│   │       │   ├── MainActivity.kt
│   │       │   └── BloodDonationApp.kt
│   │       ├── res/
│   │       │   ├── values/
│   │       │   │   ├── strings.xml
│   │       │   │   ├── colors.xml
│   │       │   │   └── styles.xml
│   │       │   └── ...
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Installation

### Prerequisites
- Android Studio 2022.1 or later
- Java 8 or later
- Android SDK 34

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/mehrdivai144-crypto/blood-donation-app.git
   cd blood-donation-app
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Click "Open"

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on emulator or device**
   - Connect an Android device or start an emulator
   - Click "Run" or use:
   ```bash
   ./gradlew installDebug
   ```

## Usage

### Tab 1: রক্তদাতা (Donors)
- Search donors by name or location
- Filter by blood type using chips
- Click "যোগাযোগ করুন" to contact a donor

### Tab 2: জরুরি চাহিদা (Emergency Requests)
- View active emergency blood requests
- See hospital and patient details
- Click to confirm if you can donate

### Tab 3: ম্যাচিং যাচাই (Compatibility Checker)
- Select your blood type
- View compatible blood types
- See who can donate to you and vice versa

### Floating Action Button
- Click "নতুন দাতা নিবন্ধন" to register as a new donor
- Fill in your details and submit

## Sample Data

The app comes with sample donor and emergency request data for testing:

**Donors:**
- ডা. সাজিদ মাহমুদ (O-, Dhaka)
- তানভীর আহমেদ (O+, Dhaka)
- রাকিবুল হাসান (B+, Chittagong)
- সাইফুল ইসলাম (A+, Dhaka)

**Emergency Requests:**
- আব্দুর রহিম (O-, Square Hospital)
- নাসরিন আক্তার (A+, Chittagong Medical)

## Build Information

- **Version Code**: 1
- **Version Name**: 1.0.0
- **Package Name**: com.example.blooddonation

## Permissions

The app requires the following permissions:
- `INTERNET` - For potential backend API calls
- `CALL_PHONE` - To initiate phone calls to donors
- `SEND_SMS` - To send SMS messages (optional)

## Future Enhancements

- Backend API integration with Firebase
- Real-time push notifications
- User authentication
- Donor history and statistics
- Map integration for nearby donors
- Rating and review system
- Multi-language support (Hindi, Urdu, etc.)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact & Support

- **Developer**: Mehrdivai144-Crypto
- **Repository**: https://github.com/mehrdivai144-crypto/blood-donation-app
- **Issues**: https://github.com/mehrdivai144-crypto/blood-donation-app/issues

## Screenshots

### Donor List Screen
- Dark theme with red accent colors
- Search bar with filters
- Donor cards with blood type badges
- Contact button for each donor

### Emergency Screen
- Alert banner for urgent requests
- Red-highlighted emergency cards
- Hospital details and time limits

### Blood Compatibility Screen
- Interactive blood type selector
- Donation compatibility information
- Clean card-based layout

---

**Made with ❤️ to save lives through blood donation**
