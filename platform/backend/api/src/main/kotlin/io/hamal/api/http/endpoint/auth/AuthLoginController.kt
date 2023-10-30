package io.hamal.api.http.endpoint.auth

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.AuthLoginPort
import io.hamal.lib.sdk.api.ApiLoginReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthLoginSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLoginController(
    private val login: AuthLoginPort
) {
    @PostMapping("/v1/login")
    fun login(@RequestBody req: ApiLoginReq): ResponseEntity<ApiSubmitted> {
        return login(req, AuthLoginSubmitted::accepted)
    }
}