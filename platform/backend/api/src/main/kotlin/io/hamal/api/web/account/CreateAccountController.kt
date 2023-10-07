package io.hamal.api.web.account


import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.CreateAccountPort
import io.hamal.lib.sdk.api.ApiCreateAccountReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateAccountController(private val createAccount: CreateAccountPort) {
    @PostMapping("/v1/accounts")
    fun createFunc(
        @RequestBody req: ApiCreateAccountReq
    ): ResponseEntity<ApiSubmittedReq> = createAccount(req) { submittedReq ->
        ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
    }
}