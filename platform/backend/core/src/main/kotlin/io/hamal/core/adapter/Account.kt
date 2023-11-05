package io.hamal.core.adapter

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.AccountId
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.submitted_req.AccountConvertSubmitted
import io.hamal.repository.api.submitted_req.AccountCreateSubmitted
import io.hamal.request.ConvertAnonymousAccountReq
import io.hamal.request.CreateAccountReq
import io.hamal.request.CreateAnonymousAccountReq
import org.springframework.stereotype.Component

interface AccountCreatePort {
    operator fun <T : Any> invoke(req: CreateAccountReq, responseHandler: (AccountCreateSubmitted) -> T): T
}

interface AccountCreateAnonymousPort {
    operator fun <T : Any> invoke(req: CreateAnonymousAccountReq, responseHandler: (AccountCreateSubmitted) -> T): T
}

interface AccountConvertAnonymousPort {
    operator fun <T : Any> invoke(
        accountId: AccountId,
        req: ConvertAnonymousAccountReq,
        responseHandler: (AccountConvertSubmitted) -> T
    ): T
}

interface AccountGetPort {
    operator fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T): T
}

interface AccountListPort {
    operator fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T): T
}

interface AccountPort : AccountCreatePort, AccountCreateAnonymousPort, AccountConvertAnonymousPort, AccountGetPort,
    AccountListPort

@Component
class AccountAdapter(
    private val submitRequest: SubmitRequest,
    private val accountQueryRepository: AccountQueryRepository
) : AccountPort {

    override fun <T : Any> invoke(req: CreateAccountReq, responseHandler: (AccountCreateSubmitted) -> T) =
        responseHandler(submitRequest(req))

    override fun <T : Any> invoke(req: CreateAnonymousAccountReq, responseHandler: (AccountCreateSubmitted) -> T) =
        responseHandler(submitRequest(req))

    override fun <T : Any> invoke(
        accountId: AccountId,
        req: ConvertAnonymousAccountReq,
        responseHandler: (AccountConvertSubmitted) -> T
    ): T = responseHandler(submitRequest(accountId, req))


    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T) =
        responseHandler(accountQueryRepository.get(accountId))

    override fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T) =
        responseHandler(accountQueryRepository.list(query))
}