plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:meta"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")

    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}

tasks.withType<Jar>() {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}