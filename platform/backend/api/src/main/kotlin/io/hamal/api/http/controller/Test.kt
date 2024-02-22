package io.hamal.api.http.controller

import io.hamal.core.security.SecurityContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/v1/test")
    fun test() {
        println(SecurityContext.current)
    }
}