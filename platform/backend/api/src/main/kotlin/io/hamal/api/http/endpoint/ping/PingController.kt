package io.hamal.admin.web.ping

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class PingController {
    @GetMapping("/v1/ping")
    fun pong() = ResponseEntity("p0ng", HttpStatus.OK)
}