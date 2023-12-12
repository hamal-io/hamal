package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthLoginEmailPort
import io.hamal.lib.sdk.api.ApiLoginEmailReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthLoginEmailSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLoginEmailController(
    private val login: AuthLoginEmailPort
) {
    @PostMapping("/v1/login")
    fun login(@RequestBody req: ApiLoginEmailReq): ResponseEntity<ApiSubmitted> {
        return login(req, AuthLoginEmailSubmitted::accepted)
    }
}