package io.hamal.api.http.controller.account


import io.hamal.api.http.auth.AuthContextHolder
import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AccountConvertAnonymousPort
import io.hamal.core.component.Retry
import io.hamal.lib.sdk.api.ApiAnonymousAccountConvertReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.Submitted
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
        @RequestBody req: ApiAnonymousAccountConvertReq
    ): ResponseEntity<ApiSubmitted> = retry {
        convert(AuthContextHolder.get().accountId, req, Submitted::accepted)
    }
}