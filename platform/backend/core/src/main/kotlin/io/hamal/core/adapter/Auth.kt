package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.submitted_req.AuthSignInWithPasswordSubmitted
import io.hamal.request.SignInReq
import org.springframework.stereotype.Component


interface AuthSignInPort {
    operator fun <T : Any> invoke(
        req: SignInReq,
        responseHandler: (AuthSignInWithPasswordSubmitted) -> T
    ): T
}

interface AuthPort : AuthSignInPort


@Component
class AuthAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val submitRequest: SubmitRequest
) : AuthPort {

    override operator fun <T : Any> invoke(
        req: SignInReq,
        responseHandler: (AuthSignInWithPasswordSubmitted) -> T
    ): T {
        val account = accountQueryRepository.find(req.name) ?: throw NoSuchElementException("Account not found")
        val password = req.password
        return responseHandler(submitRequest(account, password))
    }
}