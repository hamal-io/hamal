package io.hamal.admin.web.account


import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.sdk.admin.AdminCreateAccountReq
import io.hamal.lib.sdk.admin.AdminSubmittedWithTokenReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateAccountRoute(
    private val request: SubmitAdminRequest,
) {
    @PostMapping("/v1/accounts")
    fun createFunc(
        @RequestBody createAccount: AdminCreateAccountReq
    ): ResponseEntity<AdminSubmittedWithTokenReq> {
        val result = request(createAccount)
        return ResponseEntity(
            result.let {
                AdminSubmittedWithTokenReq(
                    reqId = it.reqId,
                    status = it.status,
                    token = it.token
                )
            }, ACCEPTED
        )
    }
}