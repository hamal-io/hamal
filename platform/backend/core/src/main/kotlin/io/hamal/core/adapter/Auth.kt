package io.hamal.core.adapter

import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.*
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import org.springframework.stereotype.Component


interface AuthLoginEmailPort {
    operator fun <T : Any> invoke(
        req: AuthLogInEmailRequest,
        responseHandler: (AuthLoginEmailRequested) -> T
    ): T
}

interface AuthLoginMetaMaskPort {
    operator fun <T : Any> invoke(
        req: AuthLogInMetaMaskRequest,
        responseHandler: (AuthLoginMetaMaskRequested) -> T
    ): T
}

interface AuthChallengeMetaMaskPort {
    operator fun invoke(req: AuthChallengeMetaMaskRequest): Web3Challenge
}

interface AuthPort : AuthChallengeMetaMaskPort, AuthLoginMetaMaskPort, AuthLoginEmailPort

@Component
class AuthAdapter(
    private val createAccount: AccountCreateMetaMaskPort,
    private val accountQueryRepository: AccountQueryRepository,
    private val authRepository: AuthRepository,
    private val encodePassword: EncodePassword,
    private val generateDomainId: GenerateId,
    private val generateToken: GenerateToken,
    private val requestCmdRepository: RequestCmdRepository,
    private val workspaceList: WorkspaceListPort
) : AuthPort {

    override fun invoke(req: AuthChallengeMetaMaskRequest): Web3Challenge {
        // FIXME 138 - append challenge based on address
        return Web3Challenge("challenge123")
    }

    override fun <T : Any> invoke(
        req: AuthLogInMetaMaskRequest,
        responseHandler: (AuthLoginMetaMaskRequested) -> T
    ): T {
        // FIXME 138 - verify signature
        val auth = authRepository.find(req.address)
        if (auth == null) {
            val submitted = createAccount(object : AccountCreateMetaMaskRequest {
                override val id: AccountId = generateDomainId(::AccountId)
                override val address: Web3Address = req.address
            }) { it }

            return AuthLoginMetaMaskRequested(
                id = generateDomainId(::RequestId),
                status = RequestStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = submitted.accountId,
                workspaceIds = listOf(submitted.workspaceId),
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(requestCmdRepository::queue).let(responseHandler)

        } else {
            return AuthLoginMetaMaskRequested(
                id = generateDomainId(::RequestId),
                status = RequestStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = auth.accountId,
                workspaceIds = workspaceList(auth.accountId) { workspaces -> workspaces.map(Workspace::id) },
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(requestCmdRepository::queue).let(responseHandler)
        }
    }

    override operator fun <T : Any> invoke(
        req: AuthLogInEmailRequest,
        responseHandler: (AuthLoginEmailRequested) -> T
    ): T {
        val auth = authRepository.find(req.email) ?: throw NoSuchElementException("Account not found")
        val account = accountQueryRepository.find(auth.accountId) ?: throw NoSuchElementException("Account not found")

        val encodedPassword = encodePassword(req.password, account.salt)
        val match = authRepository.list(account.id).filterIsInstance<EmailAuth>().any { it.hash == encodedPassword }
        if (!match) {
            throw NoSuchElementException("Account not found")
        }

        return AuthLoginEmailRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            authId = generateDomainId(::AuthId),
            accountId = account.id,
            workspaceIds = workspaceList(account.id) { workspaces -> workspaces.map(Workspace::id) },
            hash = encodedPassword,
            token = generateToken()
        ).also(requestCmdRepository::queue).let(responseHandler)
    }
}