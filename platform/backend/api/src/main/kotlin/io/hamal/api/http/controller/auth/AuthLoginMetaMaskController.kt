package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthChallengeMetaMaskPort
import io.hamal.core.adapter.AuthLoginMetaMaskPort
import io.hamal.lib.sdk.api.ApiAuthChallengeMetaMaskRequest
import io.hamal.lib.sdk.api.ApiAuthLoginMetaMaskRequest
import io.hamal.lib.sdk.api.ApiChallengeMetaMask
import io.hamal.lib.sdk.api.ApiRequested
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
    fun challenge(@RequestBody req: ApiAuthChallengeMetaMaskRequest): ResponseEntity<ApiChallengeMetaMask> {
        return ResponseEntity.ok(ApiChallengeMetaMask(metaMaskChallenge(req)))
    }

    @PostMapping("/v1/metamask/token")
    fun login(@RequestBody req: ApiAuthLoginMetaMaskRequest): ResponseEntity<ApiRequested> {
        return metamaskToken(req).accepted()
    }

}