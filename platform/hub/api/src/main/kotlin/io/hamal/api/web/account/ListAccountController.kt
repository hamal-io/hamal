package io.hamal.api.web.account

import io.hamal.core.component.account.ListAccount
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.hub.HubAccountList
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListAccountController(private val listAccount: ListAccount) {
    @GetMapping("/v1/accounts")
    fun listAccount(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: AccountId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubAccountList> {
        return listAccount(
            AccountQuery(
                afterId = afterId,
                limit = limit
            )
        ) { accounts ->
            ResponseEntity.ok(HubAccountList(
                accounts.map { account ->
                    HubAccountList.Account(
                        id = account.id,
                        name = account.name
                    )
                }
            ))
        }
    }
}