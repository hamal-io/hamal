package io.hamal.api.http.controller.account

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.account.PasswordChangePort
import io.hamal.core.component.Retry
import io.hamal.lib.sdk.api.ApiChangePasswordRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountUpdateController(
    private val retry: Retry,
    private val accountUpdate: PasswordChangePort
) {
    @PatchMapping("/v1/accounts/password")
    fun changePassword(
        @RequestBody req: ApiChangePasswordRequest
    ): ResponseEntity<ApiRequested> = retry {
        accountUpdate(req).accepted()
    }
}
