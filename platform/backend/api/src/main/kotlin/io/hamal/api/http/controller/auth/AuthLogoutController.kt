package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthLogoutPort
import io.hamal.lib.sdk.api.ApiLogoutReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthLogoutSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLogoutController(
    private val logout: AuthLogoutPort
) {
    @PostMapping("/v1/logout")
    fun logout(@RequestBody req: ApiLogoutReq): ResponseEntity<ApiSubmitted> {
        //TODO-72 if (logout) return ResponseEntity<Forbidden> 403
        return logout(req, AuthLogoutSubmitted::accepted)
    }
}