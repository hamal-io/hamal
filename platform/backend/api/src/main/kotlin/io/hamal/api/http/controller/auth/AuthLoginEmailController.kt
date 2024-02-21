package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthLoginEmailPort
import io.hamal.lib.sdk.api.ApiAuthLoginEmailRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLoginEmailController(
    private val loginWithEmail: AuthLoginEmailPort
) {
    @PostMapping("/v1/login")
    fun login(@RequestBody req: ApiAuthLoginEmailRequest): ResponseEntity<ApiRequested> {
        return loginWithEmail(req).accepted()
    }
}