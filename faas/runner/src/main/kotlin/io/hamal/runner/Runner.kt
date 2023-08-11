package io.hamal.runner

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Runner

fun main(args: Array<String>) {
    runApplication<Runner>(*args) {
        setBannerMode(Banner.Mode.OFF)
    }
}