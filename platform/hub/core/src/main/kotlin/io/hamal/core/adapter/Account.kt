package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.submitted_req.SubmittedReq
import io.hamal.request.CreateAccountReq
import org.springframework.stereotype.Component

interface CreateAccountPort {
    operator fun <T : Any> invoke(req: CreateAccountReq, responseHandler: (SubmittedReq) -> T): T
}

interface GetAccountPort {
    operator fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T): T
}

interface ListAccountPort {
    operator fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T): T
}

interface AccountPort : CreateAccountPort, GetAccountPort, ListAccountPort

@Component
class AccountAdapter(
    private val submitRequest: SubmitRequest,
    private val accountQueryRepository: AccountQueryRepository
) : AccountPort {

    override fun <T : Any> invoke(req: CreateAccountReq, responseHandler: (SubmittedReq) -> T) =
        responseHandler(submitRequest(req))

    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T) =
        responseHandler(accountQueryRepository.get(accountId))

    override fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T) =
        responseHandler(accountQueryRepository.list(query))
}