package io.hamal.extension.unsafe.net.http.web

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestHeaderController {
    @RequestMapping("/v1/headers")
    fun execute(
        @RequestHeader headers: HttpHeaders
    ): ResponseEntity<Unit> {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .headers(headers)
            .build()
    }
}