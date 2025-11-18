# The PLUGD Application 

<img width="463" height="176" alt="Screenshot 2025-09-22 at 18 31 22" src="https://github.com/user-attachments/assets/9bd83970-da71-4ac2-915b-00109700387b" />

_A Smart Platform to Discover, Connect, and Experience Events._  

Created by **Emma Jae Dunn**  
Student Number: **ST10301125**  
Module: **Programming 3D - PROG7314**  

**Youtube Demonstration Video:** 

**GitHub Link** https://github.com/VCCT-PROG7314-2025-G1/ThePLUGDPlatform.git

---

## Overview  
PLUGD is a mobile application designed to help users **discover events, artists, and organizers** in a simple and interactive way. The PLUGD App is intended to be an innovative and dynamic platform that unifies artist promotion & recognition, event discovery, brand partnership & sponsoring and community
engagement for a vibrant single ecosystem. The PLUGD platform revolutionizes creativity and discovery with a virtually assisted approach, emphasizing community and diversity for emerging talent and
professionals.

It is built entirely with **Kotlin** and **Jetpack Compose (Material 3)** in Android Studio, making it modern, scalable, and easy to maintain.  

The app was developed as part of the **PROG7312** module to demonstrate skills in:  
- Declarative UI with Jetpack Compose  
- State management  
- Navigation between screens  
- Data synchronization (local + remote repository pattern)  
- Applying good UI/UX principles (search, filters, navigation bars)  

---

## Purpose of the App
The primary goals of **The PLUGD Platform** are:

1. **Seamless Authentication** – Users can register, login, and reset passwords securely using Firebase Authentication.  
2. **Event Management** – Users can view, create, and join events within their community.  
3. **Task Tracking** – The app allows users to manage personal tasks or shared activities.  
4. **User Profiles** – Each user has a customizable profile with personalized data.  
5. **Offline Persistence** – Local caching using Room ensures offline access and fast retrieval.  

**Target Users:** Students, community organizers, and hobbyists who want to stay connected and engaged.

---

## Key Features  

#### 🔐 Authentication & User Security
- Firebase Authentication (Email/Password)
- Biometric Authentication (Fingerprint)
- Secure credential storage with EncryptedSharedPreferences
- Google Sign-in SSO

#### 📣 Notifications
- Real-time push notifications via Firebase Cloud Messaging (FCM)
- Daily reminder notifications (AlarmManager + BroadcastReceiver)
- Settings-based notification permissions & toggles

#### 🎭 Profile Hub
- User profiles with bio, followers, and following
- Upload profile picture (Firebase Storage)
- Social + music platform links (Instagram, Apple Music, SoundCloud)
- Spotify API integration to show recently played playlists

#### 👯 Social Features
- Follow system (follow requests, notifications)
- User profile hubs with bio, socials, Spotify integration
- Edit profile with image upload

#### 📌 Event (PLUG) System
- Create & edit events
- Upload supporting documents
- Full event details page (date, category, description, map location)
- Google Maps API for location search
- Search & advanced filtering system

#### 💬 Community Channels & Messaging
- Real-time group chats (Firestore)
- Replies, media attachments, recations and interactions
- Community Settings for push notifications
- Offline caching using Room Database

#### 🌍 Language Support
- English
- Afrikaans
- Xhosa

Users can change language in Settings.

#### 🌓 UI/UX Personalisation
- Dark Mode / Light Mode toggle
- Updated Material 3 theme and custom typography
- Local preferences persistence (DataStore)

#### 📡 Offline Persistence
- Room local caching for: Activities, Events, User profiles, Messaging history 

---

## Architecture  
The app follows **MVVM (Model-View-ViewModel)** with **Repository Pattern**:  

- **Firebase Firestore** for real-time data
- **Room Database** for local caching
- **Retrofit API layer** (prepared for future backend integrations)
- **Jetpack Compose Navigation**

---

## Tech Stack  

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Database:** Firebase Firestore, Room, DataStore
- **Media:** Firebase Sorage
- **REST API:** Spotify Web API & Retrofit 
- **Maps & Location:** Google Maps & Places API
- **Notifications:** Firebase Cloud Messaging, AlarmManager
- **Authentication:** Firebase Auth + BiometricPrompt API
	
---

## Project Structure  

- **ui/** Composables, Screens, Navigation
- **viewmodel/** ViewModels for managing UI state
- **repository/** Handles data access (local DB + Firebase)
- **data/** Models, DAOs, and Entities
- **utils/** Helper functions, extensions, constants
- **network/** API services and network configurations
- **res/** Layouts, drawables, strings, and themes
- **MainActivity.kt** Entry point of the app

## Design Considerations

When designing The PLUGD Platform, the following principles were prioritized:

| Consideration          | Implementation                                                                 |
|------------------------|-------------------------------------------------------------------------------|
| **User-Friendly UI**    | Jetpack Compose for responsive and modern UI components                        |
| **Scalable Architecture** | MVVM architecture with repository pattern for separation of concerns          |
| **Offline Support**     | Room Database for caching user and event data                                  |
| **Secure Authentication** | Firebase Authentication ensures secure login and registration workflows       |
| **Maintainable Codebase** | Modularized code and proper use of ViewModels and Compose navigation          |
| **Reactive Design**     | State management with Compose and LiveData/StateFlow for real-time updates     |

**Navigation:**  
- The app uses `AppNavHost` and `MainScreenNavGraph` to handle authentication flow and main app flow separately.  
- Screens include: Login, Register, Forgot Password, Home, Event Details, Add Plug (Event), Actvity (User feed), Profile, Settings, Community, Chat Screen, About/Support Page.  

**App Screens Screenshots:** View in Main Project Folder under 'App Screens'

---

## GitHub and CI/CD Utilisation

The project uses **GitHub** for version control and collaboration. 
Key features include:

1. **Branch Management**  
   - `main` branch for production-ready code  
   - `develop` branch for feature integration  
   - Feature branches for specific tasks (e.g., `feature/authentication`)

2. **GitHub Actions**  
   - Automates build, test, and deployment workflows  
   - Ensures that pull requests pass all unit tests and static analysis before merging  
   - Example workflow:  
     - **Trigger:** On push or pull request  
     - **Jobs:**  
       1. Setup JDK and Kotlin environment  
       2. Build the Android project  
       3. Run unit tests  
       4. Upload artifacts for review  

**CI Badge:**  
[![Android CI](https://github.com/emmajaedunn/PLUGDApplication/actions/workflows/android-ci.yml/badge.svg)](https://github.com/emmajaedunn/PLUGDApplication/actions/workflows/android-ci.yml)

**GitHub Workflow (Testing):**

name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        distribution: temurin
        java-version: 17
    - name: Build project
      run: ./gradlew build
    - name: Run tests
      run: ./gradlew test

<img width="939" height="559" alt="image" src="https://github.com/user-attachments/assets/8b2546e6-c596-4209-8f5e-873808d47fa5" />

## Installation and Setup 
1. Clone the repository: git clone [https://github.com/emmajaedunn/ThePLUGDPlatform.git](https://github.com/VCCT-PROG7314-2025-G1/ThePLUGDPlatform.git)
2. Open the project in Android Studio.
3. Add API Keys - Create a local secrets.properties file:
- MAPS_API_KEY="AIzaSyCNR_70bFeAZMeRtOWOUb4Cza_Zn35x7Es"
- SPOTIFY_CLIENT_ID="90806edf5fdc4928a6fcc4a3a40b5ff2"
- SPOTIFY_CLIENT_SECRET="plugd://spotify-callback"
4. Configure Firebase and add google-services.json.
5. Build and run the app on emulator or device.

## Future Enhancements

- **DM Private Messaging** - Add 1-on-1 chat with typing indicators, delivered/read receipts.
- **Event Ticketing System** - QR code event entry, Ticket scanning for organisers.
- **Monetisation** - Promotion of artists, Premium events, Featured posts, Paid opportunities
- **AI Recommendations** - Artist suggestions, Event matching based on mood/genre
- **Gamification** - User Badges & Achievements for activity, engagement, or verified artists.
- **Advanced Moderation Tools** - Admin roles, Community analytics, Content flagging system
