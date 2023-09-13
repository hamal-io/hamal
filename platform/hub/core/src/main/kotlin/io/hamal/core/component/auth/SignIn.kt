package io.hamal.core.component.auth

import io.hamal.core.req.SubmitRequest
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.submitted_req.SubmittedSignInWithPasswordReq
import io.hamal.request.SignInReq
import org.springframework.stereotype.Component

@Component
class SignIn(
    private val accountQueryRepository: AccountQueryRepository,
    private val submitRequest: SubmitRequest
) {

    operator fun <T : Any> invoke(
        req: SignInReq,
        responseHandler: (SubmittedSignInWithPasswordReq) -> T
    ): T {
        val account = accountQueryRepository.find(req.name) ?: throw NoSuchElementException("Account not found")
        val password = req.password
        return responseHandler(submitRequest(account, password))
    }
}