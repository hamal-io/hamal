package io.hamal.core.request.handler.auth

import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.EmailAuth
import io.hamal.lib.domain.request.AuthLoginEmailRequested
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit.DAYS

@Component
class LoginEmailHandler(
    private val authRepository: AuthRepository
) : io.hamal.core.request.RequestHandler<AuthLoginEmailRequested>(AuthLoginEmailRequested::class) {

    override fun invoke(req: AuthLoginEmailRequested) {
        authRepository.list(req.accountId).filterIsInstance<EmailAuth>().find { it.hash == req.hash }?.let { auth ->
            authRepository.create(
                CreateTokenAuthCmd(
                    id = req.cmdId(),
                    authId = req.authId,
                    accountId = auth.accountId,
                    token = req.token,
                    expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(30, DAYS))
                )
            )
        } ?: throw NoSuchElementException("Account not found")
    }
}