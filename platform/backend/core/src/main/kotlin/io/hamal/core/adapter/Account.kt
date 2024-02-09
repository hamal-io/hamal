package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.AccountType.Anonymous
import io.hamal.lib.domain.vo.AccountType.User
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AccountQueryRepository.AccountQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface AccountCreatePort {
    operator fun <T : Any> invoke(req: AccountCreateRequest, responseHandler: (AccountCreateRequested) -> T): T
}

interface AccountCreateMetaMaskPort {
    operator fun <T : Any> invoke(
        req: AccountCreateMetaMaskRequest,
        responseHandler: (AccountCreateMetaMaskRequested) -> T
    ): T
}

interface AccountCreateRootPort {
    operator fun invoke(req: AccountCreateRootRequest)
}

interface AccountCreateAnonymousPort {
    operator fun <T : Any> invoke(
        req: AccountCreateAnonymousRequest,
        responseHandler: (AccountCreateAnonymousRequested) -> T
    ): T
}

interface AccountConvertAnonymousPort {
    operator fun <T : Any> invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousRequest,
        responseHandler: (AccountConvertRequested) -> T
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
    private val generateDomainId: GenerateId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val requestCmdRepository: RequestCmdRepository
) : AccountPort {

    override fun <T : Any> invoke(req: AccountCreateRequest, responseHandler: (AccountCreateRequested) -> T): T {
        val salt = generateSalt()
        return AccountCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = generateDomainId(::AccountId),
            accountType = User,
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        req: AccountCreateAnonymousRequest,
        responseHandler: (AccountCreateAnonymousRequested) -> T
    ): T {
        val salt = generateSalt()
        return AccountCreateAnonymousRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = req.id,
            accountType = Anonymous,
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(
        req: AccountCreateMetaMaskRequest,
        responseHandler: (AccountCreateMetaMaskRequested) -> T
    ): T {
        return AccountCreateMetaMaskRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = req.id,
            accountType = User,
            groupId = generateDomainId(::GroupId),
            flowId = generateDomainId(::FlowId),
            salt = generateSalt(),
            address = req.address,
            metamaskAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            token = generateToken()
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun invoke(req: AccountCreateRootRequest) {
        val salt = generateSalt()

        accountQueryRepository.find(AccountId.root)
            ?: run {
                AccountCreateRequested(
                    id = generateDomainId(::RequestId),
                    status = Submitted,
                    accountId = AccountId.root,
                    accountType = AccountType.Root,
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
                ).also(requestCmdRepository::queue)
            }
    }

    override fun <T : Any> invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousRequest,
        responseHandler: (AccountConvertRequested) -> T
    ): T {
        val account = accountQueryRepository.get(accountId)
        return AccountConvertRequested(
            id = generateDomainId(::RequestId),
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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (Account) -> T) =
        responseHandler(accountQueryRepository.get(accountId))

    override fun <T : Any> invoke(query: AccountQuery, responseHandler: (List<Account>) -> T) =
        responseHandler(accountQueryRepository.list(query))
}