package io.hamal.api.http.controller.account


import io.hamal.api.http.auth.AuthContextHolder
import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AccountConvertAnonymousPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.sdk.api.ApiAccountConvertAnonymousRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountConvertController(
    private val retry: Retry,
    private val convert: AccountConvertAnonymousPort
) {
    @PostMapping("/v1/anonymous-accounts/convert")
    fun convert(
        @RequestBody req: ApiAccountConvertAnonymousRequest
    ): ResponseEntity<ApiRequested> = retry {
        convert(AuthContextHolder.get().accountId, req, Requested::accepted)
    }
}