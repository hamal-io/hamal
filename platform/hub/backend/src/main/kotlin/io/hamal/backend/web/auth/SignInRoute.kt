package io.hamal.backend.web.auth

import io.hamal.backend.req.SubmitRequest
import io.hamal.lib.domain.req.SignInReq
import io.hamal.lib.sdk.hub.domain.ApiSubmittedWithTokenReq
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignInRoute(
    private val accountQueryRepository: AccountQueryRepository,
    private val submitRequest: SubmitRequest
) {
    @PostMapping("/v1/sign-in")
    fun createFunc(
        @RequestBody signIn: SignInReq
    ): ResponseEntity<ApiSubmittedWithTokenReq> {
        val account = accountQueryRepository.find(signIn.name) ?: throw NoSuchElementException("Account not found")
        val password = signIn.password ?: throw NoSuchElementException("Account not found")
        val result = submitRequest(account, password)

        return ResponseEntity(
            ApiSubmittedWithTokenReq(
                reqId = result.reqId,
                status = result.status,
                token = result.token
            ), ACCEPTED
        )
    }
}