package io.hamal.api.web.account


import io.hamal.api.web.req.Assembler
import io.hamal.core.component.account.CreateAccount
import io.hamal.lib.sdk.hub.HubCreateAccountReq
import io.hamal.lib.sdk.hub.HubSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateAccountController(private val createAccount: CreateAccount) {
    @PostMapping("/v1/accounts")
    fun createFunc(
        @RequestBody req: HubCreateAccountReq
    ): ResponseEntity<HubSubmittedReq> = createAccount(req) { submittedReq ->
        ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
    }
}