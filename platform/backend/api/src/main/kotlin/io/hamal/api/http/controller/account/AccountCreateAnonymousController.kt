package io.hamal.api.http.controller.account


import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.account.AccountCreateAnonymousPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.request.AccountCreateAnonymousRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiRequested
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
    fun create(): ResponseEntity<ApiRequested> = retry {
        val id = generateDomainId(::AccountId)
        createAccount(object : AccountCreateAnonymousRequest {
            override val id = id
        }).accepted()
    }
}