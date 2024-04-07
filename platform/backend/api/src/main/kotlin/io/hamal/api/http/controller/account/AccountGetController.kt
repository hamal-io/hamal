package io.hamal.api.http.controller.account

import io.hamal.core.adapter.account.AccountGetPort
import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiAccount
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountGetController(
    private val retry: Retry,
    private val accountGet: AccountGetPort
) {
    @GetMapping("/v1/accounts/{accountId}")
    fun get(
        @PathVariable("accountId") accountId: AccountId,
    ): ResponseEntity<ApiAccount> {
        return retry {
            accountGet(accountId).let { account ->
                ResponseEntity.ok(
                    ApiAccount(
                        id = account.id
                    )
                )
            }
        }
    }
}