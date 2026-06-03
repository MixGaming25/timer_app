# Dolce Gusto Timer

A native Android timer app for manual Dolce Gusto coffee makers that only have on/off buttons.

## Features

- Modern Jetpack Compose UI with Material 3 styling.
- Light, dark, and system theme modes.
- Searchable capsule catalogue with popular single-capsule and two-capsule drinks.
- Guided multi-stage timers for drinks that use separate milk and coffee capsules.
- Adjustable flow speed from 5 to 12 seconds per Dolce Gusto bar, saved locally.
- Stage reset, pause, stop, and visual countdown ring.

## Build Setup

Open this folder in Android Studio, then let Android Studio install any missing Android SDK packages.

Recommended local setup:

- Android Studio with SDK Platform 35 installed.
- JDK 17 or JDK 21 for Gradle/Android builds.

This workspace currently has JDK 25 on PATH, which Gradle/Kotlin rejects for this Android project. In Android Studio, use the bundled IDE JDK or set Gradle JDK to 17/21 in:

`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`

Build from terminal after that with:

```powershell
.\gradlew.bat :app:assembleDebug
```
