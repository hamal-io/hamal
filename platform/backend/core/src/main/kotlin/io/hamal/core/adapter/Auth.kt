package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.req.SubmitRequest
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.PasswordAuth
import io.hamal.repository.api.submitted_req.AuthLoginSubmitted
import io.hamal.request.LogInReq
import org.springframework.stereotype.Component


interface AuthLoginPort {
    operator fun <T : Any> invoke(
        req: LogInReq,
        responseHandler: (AuthLoginSubmitted) -> T
    ): T
}

interface AuthPort : AuthLoginPort


@Component
class AuthAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val submitRequest: SubmitRequest
) : AuthPort {

    override operator fun <T : Any> invoke(
        req: LogInReq,
        responseHandler: (AuthLoginSubmitted) -> T
    ): T {
        val account = accountQueryRepository.find(req.username) ?: throw NoSuchElementException("Account not found")
        val encodedPassword = encodePassword(req.password, account.salt)
        val match = authRepository.list(account.id).filterIsInstance<PasswordAuth>().any { it.hash == encodedPassword }
        if (!match) {
            throw NoSuchElementException("Account not found")
        }
        return responseHandler(submitRequest(account, encodedPassword))
    }
}