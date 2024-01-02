package io.hamal.core.req.handler.auth

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.MetaMaskAuth
import io.hamal.lib.domain.submitted.AuthLoginMetaMaskSubmitted
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class LoginMetaMaskHandler(
    private val authRepository: AuthRepository
) : ReqHandler<AuthLoginMetaMaskSubmitted>(AuthLoginMetaMaskSubmitted::class) {
    override fun invoke(req: AuthLoginMetaMaskSubmitted) {
        authRepository.list(req.accountId).filterIsInstance<MetaMaskAuth>().find { it.address == req.address }
            ?.let { auth ->
                authRepository.create(
                    CreateTokenAuthCmd(
                        id = req.cmdId(),
                        authId = req.authId,
                        accountId = auth.accountId,
                        token = req.token,
                        expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
                    )
                )
            }
            ?: throw NoSuchElementException("Account not found")
    }
}