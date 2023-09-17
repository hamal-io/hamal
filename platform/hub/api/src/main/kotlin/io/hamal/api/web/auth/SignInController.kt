package io.hamal.api.web.auth

import io.hamal.core.adapter.SignInPort
import io.hamal.lib.sdk.hub.HubSignInReq
import io.hamal.lib.sdk.hub.HubSubmittedWithTokenReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SignInController(private val signIn: SignInPort) {
    @PostMapping("/v1/sign-in")
    fun createFunc(@RequestBody req: HubSignInReq): ResponseEntity<HubSubmittedWithTokenReq> {
        return signIn(req) { submitted ->
            ResponseEntity(
                HubSubmittedWithTokenReq(
                    reqId = submitted.reqId,
                    status = submitted.status,
                    token = submitted.token
                ), ACCEPTED
            )
        }
    }
}