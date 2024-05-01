package io.hamal.core.adapter.auth

import io.hamal.core.adapter.account.AccountCreateMetaMaskPort
import io.hamal.core.adapter.request.RequestAwaitPort
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
import io.hamal.lib.web3.evm.EvmSignature
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.domain.EvmSignedMessage
import io.hamal.repository.api.Auth
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
    private val workspaceList: WorkspaceListPort,
    private val awaitRequest: RequestAwaitPort
) : AuthLoginMetaMaskPort {
    override fun invoke(req: AuthLogInMetaMaskRequest): AuthLoginMetaMaskRequested {
        verifySignature(req)

        val auth = authFind(req.address)
        if (auth == null) {

            val requested = createAccount(object : AccountCreateMetaMaskRequest {
                override val id: AccountId = generateDomainId(::AccountId)
                override val address: Web3Address = req.address
            })

            awaitRequest(requested)

            return AuthLoginMetaMaskRequested(
                requestId = generateDomainId(::RequestId),
                requestedBy = SecurityContext.currentAuthId,
                requestStatus = RequestStatus.Submitted,
                id = generateDomainId(::AuthId),
                accountId = requested.id,
                workspaceIds = listOf(requested.workspaceId),
                token = generateToken(),
                address = req.address,
                signature = req.signature
            ).also(requestEnqueue::invoke)

        } else {
            if (auth !is Auth.Account) {
                throw NoSuchElementException("Account not found")
            }

            return SecurityContext.with(auth) {
                AuthLoginMetaMaskRequested(
                    requestId = generateDomainId(::RequestId),
                    requestedBy = SecurityContext.currentAuthId,
                    requestStatus = RequestStatus.Submitted,
                    id = generateDomainId(::AuthId),
                    accountId = auth.accountId,
                    workspaceIds = workspaceList(
                        WorkspaceQuery(
                            limit = Limit.all,
                            accountIds = listOf(auth.accountId)
                        )
                    ).map(Workspace::id),
                    token = generateToken(),
                    address = req.address,
                    signature = req.signature
                ).also(requestEnqueue::invoke)
            }
        }
    }
}

internal fun verifySignature(req: AuthLogInMetaMaskRequest) {
    val challenge = ChallengeMetaMask(req.address)
    val signedMessage = EvmSignedMessage(
        data = challenge.stringValue.toByteArray(),
        signature = EvmSignature(EvmPrefixedHexString(req.signature.stringValue))
    )

    if (signedMessage.address.toPrefixedHexString().value.lowercase() != req.address.stringValue.lowercase()) {
        throw NoSuchElementException("Account not found")
    }
}

