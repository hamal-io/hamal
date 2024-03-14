package io.hamal.app.web3proxy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class Web3Proxy

fun main(args: Array<String>) {
    runApplication<Web3Proxy>(*args)
}