package io.hamal.core.req.handler.auth

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.PasswordAuth
import io.hamal.repository.api.TokenAuth
import io.hamal.repository.api.submitted_req.SubmittedSignInWithPasswordReq
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class SignInWithPasswordHandler(
    private val authRepository: AuthRepository
) : ReqHandler<SubmittedSignInWithPasswordReq>(SubmittedSignInWithPasswordReq::class) {

    override fun invoke(req: SubmittedSignInWithPasswordReq) {
        authRepository.list(req.accountId).filterIsInstance<PasswordAuth>().find { it.hash == req.hash }?.let { auth ->
            authRepository.create(
                AuthCmdRepository.CreateTokenAuthCmd(
                    id = req.cmdId(),
                    authId = req.authId,
                    accountId = auth.accountId,
                    token = req.token,
                    expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
                )
            ) as TokenAuth
        } ?: throw NoSuchElementException("Account not found")
    }

}