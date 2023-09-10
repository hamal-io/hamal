package io.hamal.admin.web.account

import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.admin.AdminAccount
import io.hamal.repository.api.AccountQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetAccountRoute(
    private val accountQueryRepository: AccountQueryRepository,
) {
    @GetMapping("/v1/accounts/{accountId}")
    fun getAccount(
        @PathVariable("accountId") accountId: AccountId,
    ): ResponseEntity<AdminAccount> {
        val result = accountQueryRepository.get(accountId)
        return ResponseEntity.ok(result.let {
            AdminAccount(
                id = it.id,
                name = it.name,
            )
        })
    }
}