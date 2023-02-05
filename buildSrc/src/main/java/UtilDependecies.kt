object UtilDepVersions {
    const val coroutines_version = "1.4.1"
    const val datastore_version = "1.0.0"
    const val coin_version = "3.3.2"
}

object UtilDep {
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${UtilDepVersions.coroutines_version}"
    const val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${UtilDepVersions.coroutines_version}"
    const val datastore_preferences = "androidx.datastore:datastore-preferences:${UtilDepVersions.datastore_version}"
    const val koin = "io.insert-koin:koin-android:${UtilDepVersions.coin_version}"
}