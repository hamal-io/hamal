package io.hamal.api.http.endpoint.account


import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.AccountCreateAnonymousPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.Submitted
import io.hamal.request.CreateAnonymousAccountReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountCreateAnonymousController(
    private val generateDomainId: GenerateDomainId,
    private val retry: Retry,
    private val createAccount: AccountCreateAnonymousPort
) {
    @PostMapping("/v1/anonymous-accounts")
    fun create(): ResponseEntity<ApiSubmitted> = retry {
        val id = generateDomainId(::AccountId)
        createAccount(object : CreateAnonymousAccountReq {
            override val id = id
            override val name = AccountName("anonymous-${id.value}")
        }, Submitted::accepted)
    }
}