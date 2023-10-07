package io.hamal.admin.web.auth

import io.hamal.core.adapter.SignInPort
import io.hamal.lib.sdk.api.ApiSignInReq
import io.hamal.lib.sdk.api.ApiSubmittedWithTokenReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SignInController(private val signIn: SignInPort) {
    @PostMapping("/v1/sign-in")
    fun createFunc(@RequestBody req: ApiSignInReq): ResponseEntity<ApiSubmittedWithTokenReq> {
        return signIn(req) { submitted ->
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