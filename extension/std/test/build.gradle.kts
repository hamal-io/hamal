plugins {
    id("hamal.extension")
}

dependencies {
    api(project(":lib:kua"))
    api(project(":faas:runner"))
    api(project(":lib:sdk"))

    api(external.junit)
    api(external.hamcrest)
}