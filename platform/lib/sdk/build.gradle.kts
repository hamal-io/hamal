plugins {
    id("hamal.common")
}


dependencies {
    api(project(":platform:lib:http"))
    api(project(":platform:lib:domain"))
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}
