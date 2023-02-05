object UiDepVersions {
    const val core_version = "1.7.0"
    const val appcompat_version = "1.6.0"
    const val material_version = "1.8.0"
    const val constraint_layout_version = "2.1.4"
    const val lifecycle_viewmodel_version = "2.5.1"
}

object UiDep {
    const val core = "androidx.core:core-ktx:${UiDepVersions.core_version}"
    const val appcompat = "androidx.appcompat:appcompat:${UiDepVersions.appcompat_version}"
    const val material = "com.google.android.material:material:${UiDepVersions.material_version}"
    const val constraint_layout =
        "androidx.constraintlayout:constraintlayout:${UiDepVersions.constraint_layout_version}"
    const val lifecycle_viewmodel =
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${UiDepVersions.lifecycle_viewmodel_version}"
}