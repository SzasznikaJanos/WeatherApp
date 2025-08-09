# WeatherApp

A modern Android weather application built with Jetpack Compose and Kotlin, following Unidirectional Data Flow (UDF) architecture patterns.

## Features

- **Real-time Weather Data**: Fetch current weather conditions for any city using OpenWeatherMap API
- **Clean Architecture**: Implements UDF pattern with reactive streams using Kotlin Flows
- **Modern UI**: Built entirely with Jetpack Compose and Material 3 design
- **Offline Support**: Local caching with Room database
- **Pull-to-Refresh**: Swipe down to refresh weather data
- **Error Handling**: Comprehensive error states with user-friendly messages
- **Dependency Injection**: Uses Hilt for clean dependency management

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Architecture**: Unidirectional Data Flow (UDF) with reactive streams
- **Networking**: Retrofit + OkHttp + Kotlinx Serialization
- **Database**: Room with KSP
- **Dependency Injection**: Hilt
- **Image Loading**: Coil
- **Testing**: JUnit, MockK, Turbine, Kotest

## How to Run the App

### Prerequisites
- Android Studio Iguana or later
- JDK 17 or higher (can be run with lover)
- Android SDK with minimum API level 28
- OpenWeatherMap API key (already included for demo purposes)

### Setup
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd WeatherApp
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory and open it

3. **Sync the project**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for the sync to complete

4. **Run the app**
   - Connect an Android device (API 28+) or start an emulator
   - Click the "Run" button or press `Ctrl+R` (Windows/Linux) or `Cmd+R` (Mac)

### Build Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run UI tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run all checks (lint + tests)
./gradlew check
```

## Project Structure

```
com.jcapps.weatherapp/
├── data/
│   ├── api/           # Weather API interfaces & models
│   ├── local/         # Room database & preferences
│   ├── repository/    # Repository implementations
│   └── source/        # Local & remote data sources
├── domain/
│   ├── interactors/   # Business logic
│   ├── models/        # Domain models & error types
│   └── repository/    # Repository interfaces
├── presentation/
│   ├── components/    # Reusable UI components
│   └── weather/       # Weather feature (screens, viewmodels, models)
├── di/                # Dependency injection modules
└── arch/              # UDF architecture base classes
```

## Architecture

The app follows a strict **Unidirectional Data Flow (UDF)** pattern:

1. **UI Layer**: Stateless Compose screens that render ViewState and emit Actions
2. **ViewModel**: Transforms Results → ViewState using FlowViewModel base class
3. **Interactors**: Contains business logic, transforms Actions → Results
4. **Repository**: Coordinates data from local and remote sources
5. **Data Sources**: Handle API calls and local storage

## What I'd Add With More Time

###  Architecture Improvements

- Exponential backoff retry mechanism for network failures
- Background sync when connectivity returns

#### 3. **Multi-Module Architecture**
Layer Separation & Modularization
Implement feature-based modular architecture where each feature operates as an independent module
Establish clear separation between presentation, business logic, and data layers, IN EACH FEATURE MODULE
Ensure strict boundaries between features to prevent data leakage and maintain clean interfaces

#### **What it would include:**
- **Current Location Detection**: Automatic weather for user's location
- **Location Permissions**: Proper runtime permission handling
- Weather Animations: Isolated rendering and animation logic with minimal external dependencies