package io.hamal.plugin.net.http.endpoint

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
open class TestStatusController {
    @RequestMapping("/v1/status")
    fun execute(
        @RequestParam("code") code: Int?,
        @RequestBody(required = false) body: MultiValueMap<String, String>?
    ): ResponseEntity<Unit> {
        val status = if (code != null) {
            HttpStatus.valueOf(code)
        } else {
            HttpStatus.valueOf(body!!.getFirst("code")!!.toInt())
        }
        return ResponseEntity.status(status).build()
    }
}