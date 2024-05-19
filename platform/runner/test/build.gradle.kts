plugins {
    id("hamal.extension")
}

dependencies {
    api(project(":platform:lib:kua"))
    api(project(":platform:runner:runner"))
    api(project(":platform:lib:sdk"))

    implementation(project(":platform:runner:extension:std:memoize"))
    implementation(project(":platform:runner:extension:std:once"))
    implementation(project(":platform:runner:extension:std:table"))
    implementation(project(":platform:runner:extension:std:throw"))

    api(external.junit)
    api(external.hamcrest)
}