package io.hamal.core.req.handler.auth

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.repository.api.AuthCmdRepository.CreateTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.EmailAuth
import io.hamal.lib.domain.submitted.AuthLoginEmailSubmitted
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit.DAYS

@Component
class LoginEmailHandler(
    private val authRepository: AuthRepository
) : ReqHandler<AuthLoginEmailSubmitted>(AuthLoginEmailSubmitted::class) {

    override fun invoke(req: AuthLoginEmailSubmitted) {
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