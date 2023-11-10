package io.hamal.core.req.handler.auth

import io.hamal.core.req.ReqHandler
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.submitted_req.AuthLogoutSubmitted

class LogoutHandler(
    private val authRepository: AuthRepository
) : ReqHandler<AuthLogoutSubmitted>(AuthLogoutSubmitted::class) {
    override fun invoke(req: AuthLogoutSubmitted) {
        TODO()
    }
}