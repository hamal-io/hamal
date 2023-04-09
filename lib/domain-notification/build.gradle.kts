plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.20")
}