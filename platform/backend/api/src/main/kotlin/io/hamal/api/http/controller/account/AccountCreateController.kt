package io.hamal.api.http.controller.account


import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AccountCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.sdk.api.ApiAccountCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountCreateController(
    private val retry: Retry,
    private val createAccount: AccountCreatePort
) {
    @PostMapping("/v1/accounts")
    fun create(
        @RequestBody req: ApiAccountCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createAccount(req, Requested::accepted)
    }
}