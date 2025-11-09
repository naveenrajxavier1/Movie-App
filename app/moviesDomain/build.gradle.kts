plugins {
    `java-library`
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation(libs.inject)
    implementation(libs.kotlinx.coroutines.core) // Example for coroutines
    testImplementation(libs.junit)

}