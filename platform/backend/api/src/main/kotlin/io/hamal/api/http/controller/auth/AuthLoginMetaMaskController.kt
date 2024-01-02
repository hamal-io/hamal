package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthChallengeMetaMaskPort
import io.hamal.core.adapter.AuthLoginMetaMaskPort
import io.hamal.lib.sdk.api.ApiChallengeMetaMask
import io.hamal.lib.sdk.api.ApiAuthChallengeMetaMaskReq
import io.hamal.lib.sdk.api.ApiAuthLoginMetaMaskReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.domain.submitted.AuthLoginMetaMaskSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AuthLoginMetaMaskController(
    private val metamaskToken: AuthLoginMetaMaskPort,
    private val metaMaskChallenge: AuthChallengeMetaMaskPort
) {

    @PostMapping("/v1/metamask/challenge")
    fun challenge(@RequestBody req: ApiAuthChallengeMetaMaskReq): ResponseEntity<ApiChallengeMetaMask> {
        return ResponseEntity.ok(ApiChallengeMetaMask(metaMaskChallenge(req)))
    }

    @PostMapping("/v1/metamask/token")
    fun login(@RequestBody req: ApiAuthLoginMetaMaskReq): ResponseEntity<ApiSubmitted> {
        return metamaskToken(req, AuthLoginMetaMaskSubmitted::accepted)
    }

}