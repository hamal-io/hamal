package io.hamal.backend.web.account

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.hub.ApiAccountList
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListAccountRoute(
    private val accountQueryRepository: AccountQueryRepository
) {
    @GetMapping("/v1/accounts")
    fun listAccount(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: AccountId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiAccountList> {
        val result = accountQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }

        return ResponseEntity.ok(ApiAccountList(
            result.map { account ->
                ApiAccountList.ApiSimpleAccount(
                    id = account.id,
                    name = account.name
                )
            }
        ))
    }
}