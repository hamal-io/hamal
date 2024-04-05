package io.hamal.core.adapter.account

import io.hamal.core.adapter.auth.AuthListPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountPasswordChangeRequest
import io.hamal.lib.domain.request.AccountPasswordChangeRequested
import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthRepository
import org.springframework.stereotype.Component

fun interface PasswordChangePort {
    operator fun invoke(req: AccountPasswordChangeRequest): AccountPasswordChangeRequested
}

@Component
class PasswordChangeAdapter(
    private val encodePassword: EncodePassword,
    private val accountQueryRepository: AccountQueryRepository,
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val authRepository: AuthRepository,
    private val listAuth: AuthListPort
) : PasswordChangePort {
    override fun invoke(req: AccountPasswordChangeRequest): AccountPasswordChangeRequested {
        val account = accountQueryRepository.find(SecurityContext.currentAccountId)
            ?: throw NoSuchElementException("Account not found")

        val auth = authRepository.list(account.id).filterIsInstance<Auth.Email>().firstOrNull()
            ?: throw NoSuchElementException("Account not found")

        val encodedPassword = encodePassword(req.currentPassword, account.salt)
        if (auth.hash != encodedPassword) {
            throw IllegalArgumentException("Wrong Password")
        }

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
            email = Email("")
        ).also(requestEnqueue::invoke)
    }
}