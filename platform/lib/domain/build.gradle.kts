plugins {
    id("hamal.common")
}

dependencies {
    api(project(":platform:lib:common"))
    api(external.spring.schedule)
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}


tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}