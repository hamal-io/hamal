package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.AccountCreateAnonymousRequest
import io.hamal.lib.domain.request.AccountCreateAnonymousRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.Password.Companion.Password
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface AccountCreateAnonymousPort {
    operator fun invoke(req: AccountCreateAnonymousRequest): AccountCreateAnonymousRequested
}

@Component
class AccountCreateAnonymousAdapter(
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort
) : AccountCreateAnonymousPort {
    override fun invoke(req: AccountCreateAnonymousRequest): AccountCreateAnonymousRequested {
        val salt = generateSalt()
        val workspaceId = generateDomainId(::WorkspaceId)
        return AccountCreateAnonymousRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = req.id,
            type = AccountType.Anonymous,
            workspaceId = workspaceId,
            namespaceId = NamespaceId(workspaceId.value),
            emailAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = Password(">>You-shall-not-know<<"),
                salt = salt
            ),
            salt = salt,
            token = generateToken()
        ).also(requestEnqueue::invoke)
    }

}