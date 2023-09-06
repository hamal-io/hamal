package io.hamal.api.web.account

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.sdk.hub.HubCreateAccountReq
import io.hamal.lib.sdk.hub.HubSubmittedWithTokenReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateAccountRoute(
    private val request: SubmitRequest,
) {
    @PostMapping("/v1/accounts")
    fun createFunc(
        @RequestBody createAccount: HubCreateAccountReq
    ): ResponseEntity<HubSubmittedWithTokenReq> {
        val result = request(createAccount)
        return ResponseEntity(
            result.let {
                HubSubmittedWithTokenReq(
                    reqId = it.reqId,
                    status = it.status,
                    token = it.token
                )
            }, ACCEPTED
        )
    }
}