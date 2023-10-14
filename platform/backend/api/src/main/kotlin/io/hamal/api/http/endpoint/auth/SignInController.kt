package io.hamal.api.http.endpoint.auth

import io.hamal.core.adapter.SignInPort
import io.hamal.core.component.Retry
import io.hamal.lib.sdk.api.ApiSignInReq
import io.hamal.lib.sdk.api.ApiSubmittedWithTokenReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SignInController(
    private val retry: Retry,
    private val signIn: SignInPort
) {
    @PostMapping("/v1/sign-in")
    fun createFunc(@RequestBody req: ApiSignInReq): ResponseEntity<ApiSubmittedWithTokenReq> {
        return retry {
            signIn(req) { submitted ->
                ResponseEntity(
                    ApiSubmittedWithTokenReq(
                        reqId = submitted.reqId,
                        status = submitted.status,
                        token = submitted.token
                    ), ACCEPTED
                )
            }
        }
    }
}