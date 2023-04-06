plugins {
    id("hamal.kotlin-lib-conventions")
}


dependencies {
    implementation(project(":lib:ddd"))
    implementation(project(":lib:domain-value-object"))
    implementation(project(":module:launchpad:core"))
}