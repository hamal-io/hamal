package io.hamal.core.adapter.auth

import io.hamal.core.adapter.account.AccountCreateMetaMaskPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountCreateMetaMaskRequest
import io.hamal.lib.domain.request.AuthLogInMetaMaskRequest
import io.hamal.lib.domain.request.AuthLoginMetaMaskRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.Web3Address
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component

fun interface AuthLoginMetaMaskPort {
    operator fun invoke(req: AuthLogInMetaMaskRequest): AuthLoginMetaMaskRequested
}

@Component
class AuthLoginMetaMaskAdapter(
    private val authFind: AuthFindPort,
    private val generateDomainId: GenerateDomainId,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort,
    private val createAccount: AccountCreateMetaMaskPort,
    private val workspaceList: WorkspaceListPort
) : AuthLoginMetaMaskPort {
    override fun invoke(req: AuthLogInMetaMaskRequest): AuthLoginMetaMaskRequested {
        // FIXME 138 - verify signature
        val auth = authFind(req.address)
        if (auth == null) {

            val submitted = createAccount(object : AccountCreateMetaMaskRequest {
                override val id: AccountId = generateDomainId(::AccountId)
                override val address: Web3Address = req.address
            })

            return AuthLoginMetaMaskRequested(
                id = generateDomainId(::RequestId),
                by = SecurityContext.currentAuthId,
                status = RequestStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = submitted.accountId,
                workspaceIds = listOf(submitted.workspaceId),
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(requestEnqueue::invoke)

        } else {
            return AuthLoginMetaMaskRequested(
                id = generateDomainId(::RequestId),
                by = SecurityContext.currentAuthId,
                status = RequestStatus.Submitted,
                authId = generateDomainId(::AuthId),
                accountId = auth.accountId,
                workspaceIds = workspaceList(
                    WorkspaceQuery(
                        limit = Limit.all,
                        accountId = listOf(auth.accountId)
                    )
                ).map(Workspace::id),
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(requestEnqueue::invoke)
        }
    }

}
