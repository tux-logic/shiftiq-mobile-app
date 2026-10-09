plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.tuxlogic.shiftiq.mobile"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.tuxlogic.shiftiq.mobile"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // URL para desarrollo local con emulador Android estándar (mapea a localhost:8080 del host)
            buildConfigField("String", "BASE_URL", "\"https://shiftiq-platform.onrender.com/\"")
            //http://10.0.2.2:8080/\
        }
        release {
            // URL del backend desplegado en la nube para versión de producción
            buildConfigField("String", "BASE_URL", "\"https://shiftiq-platform.onrender.com/\"")
            optimization {
                enable = true
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        // Habilita desugaring de Java time (Instant, LocalDate) para compatibilidad con minSdk 24
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true // Permite inyectar BuildConfig.BASE_URL mediante NetworkConfigModule
    }
}

dependencies {
    // ---------------------------------------------------------------------------------------------
    // DESUGARING (Compatibilidad de APIs modernas de Java en Android minSdk 24)
    // ---------------------------------------------------------------------------------------------
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // ---------------------------------------------------------------------------------------------
    // JETPACK COMPOSE & NAVEGACIÓN
    // ---------------------------------------------------------------------------------------------
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Añadido: Fuentes de Google Fonts descargables en Compose (Inter / Roboto)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Añadido: Navegación declarativa Compose para flujo de pantallas e integración de NavHost
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // ---------------------------------------------------------------------------------------------
    // COMUNICACIÓN DE RED (RETROFIT & OKHTTP)
    // ---------------------------------------------------------------------------------------------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // Añadido: Cliente OkHttp para soportar interceptores de cabecera Bearer y Authenticator síncrono
    implementation(libs.okhttp)
    // Añadido: Logger HTTP para monitorear peticiones/respuestas del backend en modo depuración
    implementation(libs.okhttp.logging)

    // ---------------------------------------------------------------------------------------------
    // PERSISTENCIA LOCAL (DATASTORE & ROOM)
    // ---------------------------------------------------------------------------------------------
    // Almacena reactivamente la sesión de usuario (accessToken, refreshToken, activeBranchId)
    implementation(libs.androidx.datastore.preferences)
    // Base de datos local Room 3 para cache y operaciones offline
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)

    // ---------------------------------------------------------------------------------------------
    // INYECCIÓN DE DEPENDENCIAS (HILT)
    // ---------------------------------------------------------------------------------------------
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    // Integra ViewModels inyectados por Hilt en composables con hiltViewModel()
    implementation(libs.androidx.hilt.navigation.compose)

    // ---------------------------------------------------------------------------------------------
    // IMÁGENES (COIL)
    // ---------------------------------------------------------------------------------------------
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // ---------------------------------------------------------------------------------------------
    // PRUEBAS UNITARIAS (TESTING)
    // ---------------------------------------------------------------------------------------------
    testImplementation(libs.junit)
    // Añadido: MockK para simular dependencias (servicios Retrofit, repositorios) en Kotlin
    testImplementation(libs.mockk)
    // Añadido: Coroutines Test para controlar el tiempo virtual con StandardTestDispatcher
    testImplementation(libs.kotlinx.coroutines.test)
    // Añadido: Turbine para probar emisiones de Flows reactivos (SessionDataStore)
    testImplementation(libs.turbine)

    // ---------------------------------------------------------------------------------------------
    // PRUEBAS INSTRUMENTADAS & DEBUG
    // ---------------------------------------------------------------------------------------------
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}