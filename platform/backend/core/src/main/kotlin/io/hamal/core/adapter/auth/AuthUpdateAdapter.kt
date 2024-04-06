package io.hamal.core.adapter.auth

import io.hamal.core.adapter.account.AccountFindAdapter
import io.hamal.core.component.EncodePassword
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AuthUpdateRequest
import io.hamal.lib.domain.request.AuthUpdateRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Auth
import org.springframework.stereotype.Component

fun interface AuthUpdatePort {
    operator fun invoke(req: AuthUpdateRequest): AuthUpdateRequested
}

@Component
class AuthUpdateAdapter(
    private val accountFind: AccountFindAdapter,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val listAuth: AuthListPort,
) : AuthUpdatePort {
    override fun invoke(req: AuthUpdateRequest): AuthUpdateRequested {

        val account = accountFind(SecurityContext.currentAccountId) ?: throw NoSuchElementException("Account not found")
        val encodedPassword = encodePassword(req.currentPassword, account.salt)

        val auth = listAuth(account.id).filterIsInstance<Auth.Email>().find {
            it.hash == encodedPassword
        } ?: throw NoSuchElementException("Wrong Password")

        return AuthUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = auth.id,
            hash = encodePassword(req.newPassword, account.salt)
        )
    }
}