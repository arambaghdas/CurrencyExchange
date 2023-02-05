object TestVersions {
    const val junit_version = "4.13.2"
    const val test_core_version = "1.5.0"
    const val ext_junit_version = "1.1.5"
    const val espresso_core_version = "3.5.1"
    const val mockk_android_version = "1.9.3"
    const val coroutines_version = "1.4.2"
    const val turbine_version = "0.12.1"
}

object TestDep {
    const val junit = "junit:junit:${TestVersions.junit_version}"
    const val test_core = "androidx.test:core:${TestVersions.test_core_version}"
    const val ext_junit = "androidx.test.ext:junit:${TestVersions.ext_junit_version}"
    const val espresso_core = "androidx.test.espresso:espresso-core:${TestVersions.espresso_core_version}"
    const val mockk_android = "io.mockk:mockk-android:${TestVersions.mockk_android_version}"
    const val kotlinx_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${TestVersions.coroutines_version}"
    const val turbine = "app.cash.turbine:turbine:${TestVersions.turbine_version}"
}