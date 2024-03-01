package io.hamal.core.adapter.auth

import io.hamal.core.adapter.account.AccountFindPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AuthLogInEmailRequest
import io.hamal.lib.domain.request.AuthLoginEmailRequested
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository.AuthQuery
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component

fun interface AuthLoginEmailPort {
    operator fun invoke(req: AuthLogInEmailRequest): AuthLoginEmailRequested
}

@Component
class AuthLoginEmailAdapter(
    private val generateDomainId: GenerateDomainId,
    private val generateToken: GenerateToken,
    private val encodePassword: EncodePassword,
    private val requestEnqueue: RequestEnqueuePort,
    private val accountFind: AccountFindPort,
    private val authFind: AuthFindPort,
    private val listAuth: AuthListPort,
    private val workspaceList: WorkspaceListPort
) : AuthLoginEmailPort {

    override fun invoke(req: AuthLogInEmailRequest): AuthLoginEmailRequested {
        val auth = authFind(req.email) ?: throw NoSuchElementException("Account not found")
        if (auth !is Auth.Account) {
            throw NoSuchElementException("Account not found")
        }
        return SecurityContext.with(auth) {
            val account = accountFind(auth.accountId) ?: throw NoSuchElementException("Account not found")

            val encodedPassword = encodePassword(req.password, account.salt)
            val match = listAuth(
                AuthQuery(
                    limit = Limit.all,
                    authIds = listOf(auth.id)
                )
            ).filterIsInstance<Auth.Email>().any { it.hash == encodedPassword }

            if (!match) {
                throw NoSuchElementException("Account not found")
            }

            AuthLoginEmailRequested(
                id = generateDomainId(::RequestId),
                by = SecurityContext.currentAuthId,
                status = RequestStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = account.id,
                workspaceIds = workspaceList(
                    WorkspaceQuery(
                        limit = Limit.all,
                        accountIds = listOf(auth.accountId)
                    )
                ).map(Workspace::id),
                hash = encodedPassword,
                token = generateToken()
            ).also(requestEnqueue::invoke)
        }
    }
}