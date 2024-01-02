package io.hamal.api.http.controller.account


import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AccountCreateAnonymousPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.Submitted
import io.hamal.request.AccountCreateAnonymousReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountCreateAnonymousController(
    private val generateDomainId: GenerateId,
    private val retry: Retry,
    private val createAccount: AccountCreateAnonymousPort
) {
    @PostMapping("/v1/anonymous-accounts")
    fun create(): ResponseEntity<ApiSubmitted> = retry {
        val id = generateDomainId(::AccountId)
        createAccount(object : AccountCreateAnonymousReq {
            override val id = id
        }, Submitted::accepted)
    }
}