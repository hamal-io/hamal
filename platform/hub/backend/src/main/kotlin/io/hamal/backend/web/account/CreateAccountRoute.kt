package io.hamal.backend.web.account

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.CreateAccountReq
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
        @RequestBody createAccount: CreateAccountReq
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