plugins {
    id("hamal.extension")
}

dependencies {
    api(project(":platform:lib:kua"))
    api(project(":platform:runner"))
    api(project(":platform:lib:sdk"))

    api(external.junit)
    api(external.hamcrest)
}