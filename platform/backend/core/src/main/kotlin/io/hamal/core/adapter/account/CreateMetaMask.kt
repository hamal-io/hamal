package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountCreateMetaMaskRequest
import io.hamal.lib.domain.request.AccountCreateMetaMaskRequested
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Component

fun interface AccountCreateMetaMaskPort {
    operator fun invoke(req: AccountCreateMetaMaskRequest): AccountCreateMetaMaskRequested
}

@Component
class AccountCreateMetaMaskAdapter(
    private val generateDomainId: GenerateDomainId,
    private val generateSalt: GenerateSalt,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort
) : AccountCreateMetaMaskPort {
    override fun invoke(req: AccountCreateMetaMaskRequest): AccountCreateMetaMaskRequested {
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateMetaMaskRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            accountId = req.id,
            accountType = AccountType.User,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
            salt = generateSalt(),
            address = req.address,
            metamaskAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            token = generateToken()
        ).also(requestEnqueue::invoke)
    }
}