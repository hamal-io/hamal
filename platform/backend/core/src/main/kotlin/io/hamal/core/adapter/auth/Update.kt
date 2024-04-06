package io.hamal.core.adapter.auth

import io.hamal.core.adapter.account.AccountFindPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AuthUpdatePasswordRequest
import io.hamal.lib.domain.request.AuthUpdatePasswordRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Auth
import org.springframework.stereotype.Component

fun interface AuthUpdatePasswordPort {
    operator fun invoke(req: AuthUpdatePasswordRequest): AuthUpdatePasswordRequested
}

@Component
class AuthUpdatePasswordAdapter(
    private val accountFind: AccountFindPort,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val listAuth: AuthListPort,
    private val requestEnqueue: RequestEnqueuePort
) : AuthUpdatePasswordPort {
    override fun invoke(req: AuthUpdatePasswordRequest): AuthUpdatePasswordRequested {
        val account = accountFind(SecurityContext.currentAccountId) ?: throw NoSuchElementException("Account not found")
        val encodedPassword = encodePassword(req.currentPassword, account.salt)

        val auth = listAuth(account.id).filterIsInstance<Auth.Email>().find {
            it.hash == encodedPassword
        } ?: throw NoSuchElementException("Wrong Password")

        return AuthUpdatePasswordRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = auth.id,
            hash = encodePassword(req.newPassword, account.salt)
        ).also(requestEnqueue::invoke)
    }
}