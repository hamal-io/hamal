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
    operator fun invoke(req: AccountCreateRequest): AccountCreateRequested
}

interface AccountCreateMetaMaskPort {
    operator fun invoke(
        req: AccountCreateMetaMaskRequest,
    ): AccountCreateMetaMaskRequested
}

interface AccountCreateRootPort {
    operator fun invoke(req: AccountCreateRootRequest)
}

interface AccountCreateAnonymousPort {
    operator fun invoke(
        req: AccountCreateAnonymousRequest,
    ): AccountCreateAnonymousRequested
}

interface AccountConvertAnonymousPort {
    operator fun invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousRequest,
    ): AccountConvertRequested
}

interface AccountGetPort {
    operator fun invoke(accountId: AccountId): Account
}

interface AccountListPort {
    operator fun invoke(query: AccountQuery): List<Account>
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

    override fun invoke(req: AccountCreateRequest): AccountCreateRequested {
        val salt = generateSalt()
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = generateDomainId(::AccountId),
            accountType = User,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
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

    override fun invoke(
        req: AccountCreateAnonymousRequest,
    ): AccountCreateAnonymousRequested {
        val salt = generateSalt()
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateAnonymousRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = req.id,
            accountType = Anonymous,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
            passwordAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = Password(">>You-shall-not-know<<"),
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(
        req: AccountCreateMetaMaskRequest,
    ): AccountCreateMetaMaskRequested {
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateMetaMaskRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            accountId = req.id,
            accountType = User,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
            salt = generateSalt(),
            address = req.address,
            metamaskAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            token = generateToken()
        ).also(requestCmdRepository::queue)
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
                    workspaceId = WorkspaceId.root,
                    namespaceId = NamespaceId.root,
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

    override fun invoke(
        accountId: AccountId,
        req: AccountConvertAnonymousRequest,
    ): AccountConvertRequested {
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
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(accountId: AccountId) = accountQueryRepository.get(accountId)

    override fun invoke(query: AccountQuery) = accountQueryRepository.list(query)
}