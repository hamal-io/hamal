package io.hamal.core.request.handler.auth

import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AuthLoginEmailRequested
import io.hamal.lib.domain.vo.ExpiresAt
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit.DAYS

@Component
class LoginEmailHandler(
    private val authRepository: AuthRepository
) : RequestHandler<AuthLoginEmailRequested>(AuthLoginEmailRequested::class) {

    override fun invoke(req: AuthLoginEmailRequested) {
        authRepository.list(req.accountId).filterIsInstance<Auth.Email>().find { it.hash == req.hash }?.let { auth ->
            authRepository.create(
                CreateTokenAuthCmd(
                    id = req.cmdId(),
                    authId = req.authId,
                    accountId = auth.accountId,
                    token = req.token,
                    expiresAt = ExpiresAt(TimeUtils.now().plus(30, DAYS))
                )
            )
        }
    }
}