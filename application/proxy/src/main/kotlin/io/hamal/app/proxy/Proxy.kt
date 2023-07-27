package io.hamal.app.proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Proxy

fun main(args: Array<String>) {
    runApplication<Proxy>(*args)
}

