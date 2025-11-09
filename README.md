# Mustang Clone

> An Android application that revolutionizes the car-sharing experience

## Project Overview

This repository contains the source code for the Mustang Clone Android application, a modern car-sharing platform that connects car owners with renters. Built with the latest Android technologies and structured with Gradle and Kotlin DSL build system.

## Key Benefits & Features

🚗 **For Car Owners**
- Easy vehicle listing and management
- Secure booking system
- Real-time availability calendar
- Automated payment processing
- Insurance coverage integration
- Rating and review system

🔑 **For Renters**
- Quick car search and filtering
- Instant booking capability
- Secure payment gateway
- In-app navigation
- 24/7 customer support access
- Digital key sharing

⚡ **Technical Features**
- Modern Material Design UI
- Offline-first architecture
- Real-time notifications
- Location-based services
- Secure authentication
- Payment gateway integration
- Image compression and caching
- Google Maps integration

## Repository Structure

```
Mustang-Clone/
├── Group Project/              # Main project directory
│   ├── app/                   # Android application module
│   │   ├── src/
│   │   │   ├── main/         # Main source directory
│   │   │   │   ├── java/     # Kotlin/Java source files
│   │   │   │   │   ├── activities/    # Activity classes
│   │   │   │   │   ├── adapters/     # RecyclerView adapters
│   │   │   │   │   ├── models/       # Data models
│   │   │   │   │   ├── services/     # Background services
│   │   │   │   │   ├── utils/        # Utility classes
│   │   │   │   │   └── viewmodels/   # ViewModel classes
│   │   │   │   ├── res/      # Resources directory
│   │   │   │   │   ├── layout/       # XML layout files
│   │   │   │   │   ├── values/       # Colors, strings, styles
│   │   │   │   │   ├── drawable/     # Images and icons
│   │   │   │   │   └── navigation/   # Navigation graphs
│   │   │   │   └── AndroidManifest.xml
│   │   │   ├── androidTest/  # Instrumentation tests
│   │   │   └── test/        # Unit tests
│   │   ├── build/           # Build outputs
│   │   └── build.gradle.kts # App-level build config
│   ├── gradle/
│   │   └── wrapper/         # Gradle wrapper files
│   ├── build.gradle.kts     # Project-level build file
│   ├── settings.gradle.kts  # Gradle settings
│   └── local.properties     # Local SDK config
└── README.md               # This file
```

## System Requirements


- Java JDK 11 or 17
- Android SDK
- Android Studio (recommended) or compatible IDE
- Android device or emulator

## Getting Started

1. Clone the repository:
```powershell
git clone https://github.com/Kalharapasan/Mustang-Clone.git
cd "Mustang Clone/Git/Group Project"
```

2. Configure Android SDK path in `local.properties`:
```properties
sdk.dir = C:\\Users\\<your-user>\\AppData\\Local\\Android\\sdk
```

3. Open in Android Studio:
   - Launch Android Studio
   - Select: File > Open
   - Navigate to and select the "Group Project" folder

## Building the Project

From PowerShell in the "Group Project" directory:

1. Build debug APK:
```powershell
.\gradlew.bat assembleDebug
```

2. Install on connected device:
```powershell
.\gradlew.bat installDebug
```

3. Run tests:
```powershell
.\gradlew.bat test
```

4. Clean build:
```powershell
.\gradlew.bat clean
```

## Development Setup

### Environment Setup

1. Ensure Android Studio is properly configured
2. Sync project with Gradle files
3. Wait for indexing and dependency resolution
4. Build and run the project

### Running the Application

1. **Development Mode**
   - Open project in Android Studio
   - Select a connected device or emulator
   - Click 'Run' (▶️) or press Shift+F10
   - The app will build and launch automatically

2. **Testing Features**
   - Car Listing: Use test credentials in `app/src/main/assets/test_data.json`
   - Payments: Use sandbox payment credentials
   - Location: Enable mock location in developer options
   - Push Notifications: Use Firebase test console

3. **Performance Monitoring**
   - Enable USB debugging for real-time monitoring
   - Check Android Studio's CPU and memory monitors
   - Use Firebase Performance Monitoring dashboard

### Debugging Tips

- Use Android Studio's Layout Inspector for UI issues
- Enable Network Inspector for API calls
- Check Logcat with filter: `tag:MustangClone`
- Use Android Device File Explorer for local storage inspection

## Release Builds

For signed release builds:
1. Create a keystore file (if not exists)
2. Configure signing in `app/build.gradle.kts`
3. Store sensitive data in `keystore.properties` (do not commit this file)
4. Run `.\gradlew.bat assembleRelease`

## Contributing

1. Fork the repository
2. Create a feature branch from `main`
3. Make your changes
4. Test thoroughly
5. Submit a Pull Request with clear description

## Troubleshooting

- **Gradle Sync Issues**: Run `.\gradlew.bat --refresh-dependencies`
- **Build Errors**: Verify JDK version and `JAVA_HOME` setting
- **SDK Issues**: Check `local.properties` SDK path
- **Device Connection**: Enable USB debugging on device

## Contact

- Owner: Kalharapasan
- Repository: [Mustang-Clone](https://github.com/Kalharapasan/Mustang-Clone)

## License

📄 [License](./LICENSE.md): Proprietary – Permission Required

---

For detailed documentation and guides, please refer to the project Wiki or contact the repository owner.