# The PLUGD Application 

<img width="463" height="176" alt="Screenshot 2025-09-22 at 18 31 22" src="https://github.com/user-attachments/assets/9bd83970-da71-4ac2-915b-00109700387b" />

_A Smart Platform to Discover, Connect, and Experience Events._  

Created by **Emma Jae Dunn**  
Student Number: **ST10301125**  
Module: **Programming 3D - PROG7314**  

**Youtube Demonstration Video:** (coming) 

**GitHub Link** https://github.com/emmajaedunn/ThePLUGDPlatform.git

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

## Features  
- **Onboarding Flow** → Guides first-time users through app introduction.  
- **Authentication** → Simple login/register screens.  
- **Event Discovery Page** → Users can browse events, apply filters, and search.  
- **Search Integration** → Search events, artists, or organizers directly from the **TopBar**.  
- **Navigation**  
  - **Bottom Navigation Bar**: Provides quick access to core sections.  
  - **Top App Bar**: Transparent with search bar, logo, and filter/menu button.  
  - **Back Button Handling**: Appears only when navigating into sub-screens (e.g., Filters, Settings).  
- **Event Synchronization** → Repository pattern allows syncing events between remote API and local database.  

---

## Screens  

### 1. Onboarding  
- Shown only on first app launch.  
- Explains core features of PLUGD.  

### 2. Authentication  
- **Login** and **Register** screens.
- Uses Firebase authentication services
- Minimal user input required (Name, Username, Email, Password).
- Additional Google SSO option 
- Additional details (e.g., Date of Birth, Account Type) captured later in the profile screen.  

### 3. Home (Discovery)  
- Displays events with filters and search functionality.  
- Updates in real-time
- Provides filters for event discovery (not yet implemented)
- View event details
 
### 4. Community 
- Already created channels where users can join
- Users can change their settings for all communities
- Channels are a chat screen where anyone can send messages (real-time)
- Users can also upload files and also react to messages with emojis 

### 5. Add Plug  
- Screen for adding your own event ("Plug").
- Able to upload a supporting doc
- Information needed: category, title, location, time etc. 
- Accessible from the home screen navigation.  

### 6. Profile 
- Displays user profile with their username, location bio etc.
- Shows the users followers (not yet implemented)
- Shows the users music & social platforms (not yet implemented)
- Upload profile picture (not yet implemented) 
- Displays the events the users have created (not yet implemented) 

### 7. Settings 
- Accessible via the profile screens or any main screen
- Edit, manage an update your information and preferences.
- Can change your password.
- Settings button/toggles for biometrics, notifcations, dark mode, langauge preference.
- Able to delete your account

---

## Architecture  
The app follows **MVVM (Model-View-ViewModel)** with **Repository Pattern**:  

### Authentication
	•	Firebase Authentication
	•	Handles register/login with Email & Password or Google Sign-In.
	•	Gives you a uid for each user (this is your unique identity).

### User Profiles
	•	Where stored: API + Room (not Firestore).
	•	Flow: On register → Create minimal UserEntity in API (with uid, name, username, email).
	•	User can update extra info (bio, location, phone, etc) → API updates & Room caches it.
	•	Reason: Profile is structured data, not real-time. Easier to keep in API.

### Events
	•	Where stored: API + Room (not Firestore).
	•	Flow:
	•	User creates an event → save to Room (offline) → sync to API when online.
	•	Events list in Home screen pulls from Room, synced with API.
	•	Reason: Events don’t need real-time speed. API is cleaner & controlled.
	
### Community Chat
	•	Where stored: Firestore (with Firebase’s built-in offline support).
	•	Flow:
	•	Each message saved in Firestore collection (/chats/{roomId}/messages).
	•	Firestore automatically syncs across devices in real-time.
	•	Reason: Chats must be real-time → Firestore is designed for this.

### Offline Support
	•	Profiles & Events → RoomDB cache.
	•	Chats → Firestore already has offline caching built-in (no need for Room).

### Repository Layer Setup
	•	AuthRepository → handles FirebaseAuth login/register.
	•	ProfileRepository → API + Room sync for profiles.
	•	EventRepository → API + Room sync for events.
	•	ChatRepository → Firestore only (for chat).

---

## Technologies Used  
- **Kotlin** (Primary language)
- **Firebase** for authentication, firestore (database), cloud messaging service (real-time messaging) and firebase storage (storing files)
- **Jetpack Compose (Material 3)** for UI (Screen and Components)
- **Navigation Component (Compose)** for in-app navigation
- **Repository** is the central data manager for events (fetching from local DB + remote API).
- **ViewModel & LiveData/StateFlow** for state management  
- **Room Database** (Local storage & Offline Mode) 
- **Coroutines** for asynchronous operations  

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

**Screenshots:**

##### Onboarding Pages


##### Regsiter Screen 


##### Login Screen 


##### Google SSO Screen 


##### Forgot Password 


##### Home/Discovery Screen


##### Filter Pages (not yet implemented)


##### Event Details Screen 


##### Community Screen 


##### Community Settings 


##### Channel Chat Screen 


##### Channel Settings 


##### Add Plug (Event)


##### Activity Screen (not yet implemented) 


##### Profile Screen 


##### Settings Screen 


##### About/Support Page 

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
![Build Status](https://github.com/<emmajaedunn>/<ThePLUGDPlatform>/actions/workflows/android-ci.yml/badge.svg)

**Workflow Screenshot:**

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
	  
![GitHub Actions Workflow](images/github_actions.png)

## Getting Started
1. Clone the repository: git clone [https://github.com/yourusername/plugd-platform.git](https://github.com/emmajaedunn/ThePLUGDPlatform.git)
2. Open the project in Android Studio.
3. Configure Firebase and add google-services.json.
4. Build and run the app on emulator or device.

## Future Enhancements for the final POE submission:
- Push notifications for real-time app updates.
- Add biometric facial recognition.
- Allow users to filter their search.
- Users can follow friends and send requests - update on profile page.
- Music & Social Platform Integration - profile page.
- Add real-time notifcations for the event alerts.
- Users can filter their searches with preferences.
- Multi-language support - Afrikaans & Xhosa.
- Dark mode support.
- Social features: chat, comments, and likes
- Integration with cloud functions for advanced backend features
