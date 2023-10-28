package io.hamal.api.http.endpoint.account


import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.AccountCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.sdk.api.ApiAccountCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountCreateController(
    private val retry: Retry,
    private val createAccount: AccountCreatePort
) {
    @PostMapping("/v1/accounts")
    fun createFunc(
        @RequestBody req: ApiAccountCreateReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createAccount(req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
    }
}