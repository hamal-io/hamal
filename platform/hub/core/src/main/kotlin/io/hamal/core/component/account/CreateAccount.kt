package io.hamal.core.component.account


import io.hamal.core.req.SubmitRequest
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateAccountReq
import org.springframework.stereotype.Component

@Component
class CreateAccount(private val submitRequest: SubmitRequest) {
    operator fun <T : Any> invoke(
        req: CreateAccountReq,
        responseHandler: (SubmittedReq) -> T
    ): T {
        return responseHandler(submitRequest(req))
    }
}