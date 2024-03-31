package io.hamal.api.http.controller.account

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.account.AccountUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiAccountUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountUpdateController(
    private val retry: Retry,
    private val accountUpdate: AccountUpdatePort
) {

    @PatchMapping("/v1/accounts/{accountId}")
    fun updateAccount(
        @PathVariable("accountId") accountId: AccountId,
        @RequestBody req: ApiAccountUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        accountUpdate(accountId, req).accepted()
    }

}