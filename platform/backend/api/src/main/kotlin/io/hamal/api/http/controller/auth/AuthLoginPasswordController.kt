package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthLoginPasswordPort
import io.hamal.lib.sdk.api.ApiLoginPasswordReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthLoginPasswordSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLoginPasswordController(
    private val login: AuthLoginPasswordPort
) {
    @PostMapping("/v1/login")
    fun login(@RequestBody req: ApiLoginPasswordReq): ResponseEntity<ApiSubmitted> {
        return login(req, AuthLoginPasswordSubmitted::accepted)
    }
}