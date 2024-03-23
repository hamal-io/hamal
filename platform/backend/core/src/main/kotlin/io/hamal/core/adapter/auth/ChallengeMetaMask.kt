package io.hamal.core.adapter.auth

import io.hamal.lib.domain.request.AuthChallengeMetaMaskRequest
import io.hamal.lib.domain.vo.Web3Address
import io.hamal.lib.domain.vo.Web3Challenge
import org.springframework.stereotype.Component

fun interface AuthChallengeMetaMaskPort {
    operator fun invoke(req: AuthChallengeMetaMaskRequest): Web3Challenge
}

@Component
class AuthChallengeMetaMaskAdapter : AuthChallengeMetaMaskPort {
    override fun invoke(req: AuthChallengeMetaMaskRequest) = ChallengeMetaMask(req.address)
}

object ChallengeMetaMask {
    operator fun invoke(address: Web3Address) = Web3Challenge(
        "Please sign this message to login: ${address.value.substring(2, 10)}"
    )
}