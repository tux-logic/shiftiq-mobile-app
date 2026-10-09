# AGENTS.md

Android app (`com.tuxlogic.shiftiq.mobile`): Kotlin + Jetpack Compose (Material3).
Currently initialized as a single-module scaffold (`:app`), transitioning to multi-module (`build-logic`, `:core:*`, `:feature:*`) according to `docs/MOBILE-ARCHITECTURE.md` and `docs/DEVELOPMENT-PLAN.md`.

## Build & test (Windows PowerShell)

Use the Gradle wrapper. On Windows call `.\gradlew.bat`; the shell `gradlew` is tracked instead.

- Build: `.\gradlew.bat :app:assembleDebug`
- Unit tests: `.\gradlew.bat :app:testDebugUnitTest`
- Single test: `.\gradlew.bat :app:testDebugUnitTest --tests "com.tuxlogic.shiftiq.mobile.ExampleUnitTest"`
- Instrumented tests (need device/emulator): `.\gradlew.bat :app:connectedDebugAndroidTest`
- Lint: `.\gradlew.bat :app:lintDebug`

There is no CI, no formatter/lint config, and no README. `local.properties` (gitignored) holds `sdk.dir`.

## Toolchain (do not downgrade)

- Gradle 9.6 (wrapper), AGP 9.4.1, Kotlin 2.4.21, KSP.
- Gradle daemon JDK toolchain pinned to 25 in `gradle/gradle-daemon-jvm.properties`; the app itself targets Java 11.
- `compileSdk`/`targetSdk` 37, `minSdk` 24.
- AGP 9 DSL is used deliberately: `compileSdk { version = release(37) }` and release `optimization { enable = true; packageScope = ... }`. Do not rewrite these to the legacy `compileSdk = 37` / `isMinifyEnabled` forms.

## Dependencies

All versions, plugins, and libraries live in `gradle/libs.versions.toml` and are referenced as `libs.*`. Add/upgrade there, never inline a version in a `build.gradle.kts`.

- Room uses the `androidx.room3` artifacts (`room3-runtime` / `room3-compiler`), not `androidx.room`.
- Hilt and Room are wired through KSP (`ksp(...)`), not kapt.
- Retrofit uses the Gson converter (Kotlin serialization plugin is declared but not used in code yet).
- Required additions for Milestone 1: `navigation-compose`, `logging-interceptor`, `desugar_jdk_libs` (for Java time desugaring with minSdk 24), and `kotlin.jvm` plugin for pure Kotlin core modules.

- `ShiftIQApplication` annotated `@HiltAndroidApp` registered via `android:name` in `AndroidManifest.xml`.
- `MainActivity` is `@AndroidEntryPoint` with Material3 `ShiftIQTheme`.
- Foundation packages implemented in `app/src/main/java/com/tuxlogic/shiftiq/mobile/core/` (`common`, `model`, `datastore`, `network`, `designsystem`, `navigation`).
- R8 keep rules go in `app/src/main/keepRules/*.keep`.
