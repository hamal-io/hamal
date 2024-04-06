package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.auth.AuthUpdatePasswordPort
import io.hamal.lib.sdk.api.ApiPasswordChangeRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthUpdateController(
    private val updateAuth: AuthUpdatePasswordPort
) {
    @PatchMapping("/v1/auth")
    fun update(@RequestBody req: ApiPasswordChangeRequest): ResponseEntity<ApiRequested> {
        return updateAuth(req).accepted()
    }
}