package io.hamal.backend.web.account

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.CreateAccountReq
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId
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
    ): ResponseEntity<ApiSubmittedReqWithId> {

        val result = request(createAccount)
        return ResponseEntity(
            result.let {
                ApiSubmittedReqWithId(
                    reqId = it.reqId,
                    status = it.status,
                    id = it.id
                )
            }, ACCEPTED
        )
    }
}