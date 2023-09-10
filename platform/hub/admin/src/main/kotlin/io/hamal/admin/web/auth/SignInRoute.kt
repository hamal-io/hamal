package io.hamal.admin.web.auth

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.sdk.hub.HubSignInReq
import io.hamal.lib.sdk.hub.HubSubmittedWithTokenReq
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class SignInRoute(
    private val accountQueryRepository: AccountQueryRepository,
    private val submitRequest: SubmitAdminRequest
) {
    @PostMapping("/v1/sign-in")
    fun createFunc(
        @RequestBody signIn: HubSignInReq
    ): ResponseEntity<HubSubmittedWithTokenReq> {
        val account = accountQueryRepository.find(signIn.name) ?: throw NoSuchElementException("Account not found")
        val password = signIn.password ?: throw NoSuchElementException("Account not found")
        val result = submitRequest(account, password)

        return ResponseEntity(
            HubSubmittedWithTokenReq(
                reqId = result.reqId,
                status = result.status,
                token = result.token
            ), ACCEPTED
        )
    }
}