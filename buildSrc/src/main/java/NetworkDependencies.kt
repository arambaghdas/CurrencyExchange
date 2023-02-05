object NetworkDepVersions {
    const val retrofit2_version = "2.9.0"
    const val okhttp3_version = "4.7.0"
}

object NetworkDep {
    const val retrofit2 = "com.squareup.retrofit2:retrofit:${NetworkDepVersions.retrofit2_version}"
    const val retrofit2_gson = "com.squareup.retrofit2:converter-gson:${NetworkDepVersions.retrofit2_version}"
    const val okhttp3 = "com.squareup.okhttp3:okhttp:${NetworkDepVersions.okhttp3_version}"
}