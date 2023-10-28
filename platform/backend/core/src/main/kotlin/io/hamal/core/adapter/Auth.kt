package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.submitted_req.AuthSignInWithPasswordSubmittedReq
import io.hamal.request.SignInReq
import org.springframework.stereotype.Component


interface SignInPort {
    operator fun <T : Any> invoke(
        req: SignInReq,
        responseHandler: (AuthSignInWithPasswordSubmittedReq) -> T
    ): T
}

interface AuthPort : SignInPort


@Component
class AuthAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val submitRequest: SubmitRequest
) : AuthPort {

    override operator fun <T : Any> invoke(
        req: SignInReq,
        responseHandler: (AuthSignInWithPasswordSubmittedReq) -> T
    ): T {
        val account = accountQueryRepository.find(req.name) ?: throw NoSuchElementException("Account not found")
        val password = req.password
        return responseHandler(submitRequest(account, password))
    }
}