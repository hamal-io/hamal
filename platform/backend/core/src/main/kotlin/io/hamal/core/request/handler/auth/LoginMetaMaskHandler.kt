package io.hamal.core.request.handler.auth

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AuthLoginMetaMaskRequested
import io.hamal.lib.domain.vo.ExpiresAt.Companion.ExpiresAt
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class LoginMetaMaskHandler(
    private val authRepository: AuthRepository
) : RequestHandler<AuthLoginMetaMaskRequested>(AuthLoginMetaMaskRequested::class) {
    override fun invoke(req: AuthLoginMetaMaskRequested) {
        authRepository.list(req.accountId).filterIsInstance<Auth.MetaMask>().find { it.address == req.address }
            ?.let { auth ->
                authRepository.create(
                    CreateTokenAuthCmd(
                        id = req.cmdId(),
                        authId = req.id,
                        accountId = auth.accountId,
                        token = req.token,
                        expiresAt = ExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
                    )
                )
            }
    }
}