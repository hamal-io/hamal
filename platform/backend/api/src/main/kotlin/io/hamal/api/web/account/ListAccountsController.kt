package io.hamal.api.web.account

import io.hamal.core.adapter.ListAccountsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiAccountList
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListAccountsController(private val listAccount: ListAccountsPort) {
    @GetMapping("/v1/accounts")
    fun listAccount(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: AccountId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiAccountList> {
        return listAccount(
            AccountQuery(
                afterId = afterId,
                limit = limit
            )
        ) { accounts ->
            ResponseEntity.ok(ApiAccountList(
                accounts.map { account ->
                    ApiAccountList.Account(
                        id = account.id,
                        name = account.name
                    )
                }
            ))
        }
    }
}