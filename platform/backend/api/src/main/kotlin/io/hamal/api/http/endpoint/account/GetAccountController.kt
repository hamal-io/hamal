package io.hamal.api.http.endpoint.account

import io.hamal.core.adapter.GetAccountPort
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiAccount
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetAccountController(private val getAccount: GetAccountPort) {
    @GetMapping("/v1/accounts/{accountId}")
    fun getAccount(
        @PathVariable("accountId") accountId: AccountId,
    ): ResponseEntity<ApiAccount> {
        return getAccount(accountId) { account ->
            ResponseEntity.ok(
                ApiAccount(
                    id = account.id,
                    name = account.name,
                )
            )
        }
    }
}