package io.hamal.core.req.handler.auth

import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.submitted_req.AuthLogoutSubmitted

class LogoutHandler(
    private val authRepository: AuthRepository
) : ReqHandler<AuthLogoutSubmitted>(AuthLogoutSubmitted::class) {
    override fun invoke(req: AuthLogoutSubmitted) {
        authRepository.list(req.accountId).firstOrNull() { it.accountId == req.accountId }?.let {//Token?
            authRepository.revokeAuth(
                AuthCmdRepository.RevokeAuthCmd(
                    id = req.cmdId(),
                    accountId = req.accountId
                )
            )
        } ?: throw NoSuchElementException("Account not found")
    }
}