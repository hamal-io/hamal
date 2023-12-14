package io.hamal.api.http.controller.auth

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.AuthChallengeMetaMaskPort
import io.hamal.core.adapter.AuthLoginMetaMaskPort
import io.hamal.lib.sdk.api.ApiChallengeMetaMask
import io.hamal.lib.sdk.api.ApiChallengeMetaMaskReq
import io.hamal.lib.sdk.api.ApiLoginMetaMaskReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.AuthLoginMetaMaskSubmitted
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
    fun challenge(@RequestBody req: ApiChallengeMetaMaskReq): ResponseEntity<ApiChallengeMetaMask> {
        return ResponseEntity.ok(ApiChallengeMetaMask(metaMaskChallenge(req)))
    }

    @PostMapping("/v1/metamask/token")
    fun login(@RequestBody req: ApiLoginMetaMaskReq): ResponseEntity<ApiSubmitted> {
        return metamaskToken(req, AuthLoginMetaMaskSubmitted::accepted)
    }

}