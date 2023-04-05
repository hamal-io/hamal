plugins {
    id("hamal.kotlin-lib-conventions")
}

dependencies{
    testImplementation(external.junit)
    testImplementation(external.hamcrest)
}