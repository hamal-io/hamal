package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountPasswordChangeRequest
import io.hamal.lib.domain.request.AccountPasswordChangeRequested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthRepository
import org.springframework.stereotype.Component

fun interface AccountPasswordChangePort {
    operator fun invoke(req: AccountPasswordChangeRequest): AccountPasswordChangeRequested
}

@Component
class AccountPasswordUpdateAdapter(
    private val encodePassword: EncodePassword,
    private val accountFind: AccountFindPort,
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val authRepository: AuthRepository
) : AccountPasswordChangePort {
    override fun invoke(req: AccountPasswordChangeRequest): AccountPasswordChangeRequested {

        val account = accountFind(SecurityContext.currentAccountId)
            ?: throw NoSuchElementException("Account not found")

        val encodedPassword = encodePassword(req.currentPassword, account.salt)
        val auth = authRepository.list(account.id).filterIsInstance<Auth.Email>().find { it.hash == encodedPassword }
            ?: throw NoSuchElementException("Wrong Password")

        val salt = generateSalt()
        return AccountPasswordChangeRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = account.id,
            hash = encodePassword(
                password = req.newPassword,
                salt = salt
            ),
            salt = salt,
            email = auth.email,
            authId = auth.id,
        ).also(requestEnqueue::invoke)
    }
}