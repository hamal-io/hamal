package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.req.req.CreateRootAccountReq
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Anonymous
import io.hamal.lib.domain.vo.AccountType.User
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.AccountConvertSubmitted
import io.hamal.repository.api.submitted_req.AccountCreateAnonymousSubmitted
import io.hamal.repository.api.submitted_req.AccountCreateMetaMaskSubmitted
import io.hamal.repository.api.submitted_req.AccountCreateSubmitted
import io.hamal.request.AccountConvertAnonymousReq
import io.hamal.request.AccountCreateReq
import io.hamal.request.AccountCreateAnonymousReq
import io.hamal.request.AccountCreateMetaMaskReq
import org.springframework.stereotype.Component

interface AccountCreatePort {
    operator fun <T : Any> invoke(req: AccountCreateReq, responseHandler: (AccountCreateSubmitted) -> T): T
}

interface AccountCreateMetaMaskPort {
    operator fun <T : Any> invoke(
        req: AccountCreateMetaMaskReq,
        responseHandler: (AccountCreateMetaMaskSubmitted) -> T
    ): T
}

interface AccountCreateRootPort {
    operator fun invoke(req: CreateRootAccountReq)
}

interface AccountCreateAnonymousPort {
    operator fun <T : Any> invoke(req: AccountCreateAnonymousReq, responseHandler: (AccountCreateAnonymousSubmitted) -> T): T
}

interface AccountConvertAnonymousPort {
    operator fun <T : Any> invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousReq,
        responseHandler: (AccountConvertSubmitted) -> T
    ): T
}

interface AccountGetPort {
    operator fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T): T
}

interface AccountListPort {
    operator fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T): T
}

interface AccountPort : AccountCreatePort,
    AccountCreateAnonymousPort,
    AccountCreateMetaMaskPort,
    AccountCreateRootPort,
    AccountConvertAnonymousPort,
    AccountGetPort,
    AccountListPort

@Component
class AccountAdapter(
    private val accountQueryRepository: AccountQueryRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateDomainId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val reqCmdRepository: ReqCmdRepository
) : AccountPort {

    override fun <T : Any> invoke(req: AccountCreateReq, responseHandler: (AccountCreateSubmitted) -> T): T {
        val salt = generateSalt()
        return AccountCreateSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = generateDomainId(::AccountId),
            type = User,
            groupId = generateDomainId(::GroupId),
            flowId = generateDomainId(::FlowId),
            email = req.email,
            passwordAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password,
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(req: AccountCreateAnonymousReq, responseHandler: (AccountCreateAnonymousSubmitted) -> T): T {
        val salt = generateSalt()
        return AccountCreateAnonymousSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = req.id,
            type = Anonymous,
            groupId = generateDomainId(::GroupId),
            flowId = generateDomainId(::FlowId),
            passwordAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = Password(">>You-shall-not-know<<"),
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        req: AccountCreateMetaMaskReq,
        responseHandler: (AccountCreateMetaMaskSubmitted) -> T
    ): T {
        return AccountCreateMetaMaskSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = req.id,
            type = User,
            groupId = generateDomainId(::GroupId),
            flowId = generateDomainId(::FlowId),
            salt = generateSalt(),
            address = req.address,
            metamaskAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            token = generateToken()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun invoke(req: CreateRootAccountReq) {
        val salt = generateSalt()

        accountQueryRepository.find(AccountId.root)
            ?: run {
                AccountCreateSubmitted(
                    id = generateDomainId(::ReqId),
                    status = Submitted,
                    accountId = AccountId.root,
                    type = AccountType.Root,
                    groupId = GroupId.root,
                    flowId = FlowId.root,
                    email = req.email,
                    passwordAuthId = generateDomainId(::AuthId),
                    tokenAuthId = generateDomainId(::AuthId),
                    hash = encodePassword(
                        password = req.password,
                        salt = salt
                    ),
                    salt = salt,
                    token = generateToken()
                ).also(reqCmdRepository::queue)
            }
    }

    override fun <T : Any> invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousReq,
        responseHandler: (AccountConvertSubmitted) -> T
    ): T {
        val account = accountQueryRepository.get(accountId)
        return AccountConvertSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            accountId = accountId,
            email = req.email,
            passwordAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password,
                salt = account.salt
            ),
            token = generateToken()
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T) =
        responseHandler(accountQueryRepository.get(accountId))

    override fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T) =
        responseHandler(accountQueryRepository.list(query))
}