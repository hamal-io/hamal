package io.hamal.api.http.endpoint.auth

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.AuthSignInPort
import io.hamal.core.component.Retry
import io.hamal.lib.sdk.api.ApiSignInReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthSignInWithPasswordSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SignInController(
    private val retry: Retry,
    private val signIn: AuthSignInPort
) {
    @PostMapping("/v1/sign-in")
    fun createFunc(@RequestBody req: ApiSignInReq): ResponseEntity<ApiSubmitted> {
        return retry {
            signIn(req, AuthSignInWithPasswordSubmitted::accepted)
        }
    }
}