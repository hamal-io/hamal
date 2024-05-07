package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.AccountCreateRequest
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface AccountCreatePort {
    operator fun invoke(req: AccountCreateRequest): AccountCreateRequested
}

@Component
class AccountCreateAdapter(
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort

) : AccountCreatePort {
    override fun invoke(req: AccountCreateRequest): AccountCreateRequested {
        val salt = generateSalt()
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            accountId = generateDomainId(::AccountId),
            accountType = AccountType.User,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
            email = req.email,
            emailAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password,
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(requestEnqueue::invoke)
    }
}