package io.hamal.backend.web.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.hub.domain.ApiAccount
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetAccountRoute(
    private val accountQueryRepository: AccountQueryRepository,
) {
    @GetMapping("/v1/accounts/{accountId}")
    fun getAccount(
        @PathVariable("accountId") accountId: AccountId,
    ): ResponseEntity<ApiAccount> {
        val result = accountQueryRepository.get(accountId)
        return ResponseEntity.ok(result.let {
            ApiAccount(
                id = it.id,
                name = it.name,
            )
        })
    }
}