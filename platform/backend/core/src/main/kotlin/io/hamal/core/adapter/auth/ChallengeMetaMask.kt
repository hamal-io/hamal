package io.hamal.core.adapter.auth

import io.hamal.lib.domain.request.AuthChallengeMetaMaskRequest
import io.hamal.lib.domain.vo.Web3Challenge
import org.springframework.stereotype.Component

fun interface AuthChallengeMetaMaskPort {
    operator fun invoke(req: AuthChallengeMetaMaskRequest): Web3Challenge
}

@Component
class AuthChallengeMetaMaskAdapter : AuthChallengeMetaMaskPort {
    override fun invoke(req: AuthChallengeMetaMaskRequest): Web3Challenge {
        // FIXME 138 - append challenge based on address
        return Web3Challenge("challenge123")
    }
}