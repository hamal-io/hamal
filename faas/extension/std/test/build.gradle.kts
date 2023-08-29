plugins {
    id("hamal.extension")
}

dependencies {
    api(project(":faas:lib:kua"))
    api(project(":faas:runner"))
    api(project(":faas:lib:sdk"))

    api(external.junit)
    api(external.hamcrest)
}