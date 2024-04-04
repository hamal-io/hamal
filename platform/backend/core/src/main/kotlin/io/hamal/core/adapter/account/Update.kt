package io.hamal.core.adapter.account

import io.hamal.core.adapter.auth.AuthListPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountUpdateRequest
import io.hamal.lib.domain.request.AccountUpdateRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthQueryRepository
import io.hamal.repository.api.AuthRepository
import org.springframework.stereotype.Component

fun interface AccountUpdatePort {
    operator fun invoke(accountId: AccountId, req: AccountUpdateRequest): AccountUpdateRequested
}

@Component
class AccountUpdateAdapter(
    private val encodePassword: EncodePassword,
    private val accountQueryRepository: AccountQueryRepository,
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val authRepository: AuthRepository,
    private val listAuth: AuthListPort
) : AccountUpdatePort {
    override fun invoke(accountId: AccountId, req: AccountUpdateRequest): AccountUpdateRequested {
        val account = accountQueryRepository.find(accountId) ?: throw NoSuchElementException("Account not found")

        val encodedPassword = encodePassword(req.currentPassword, account.salt)
        val match = listAuth(
            AuthQueryRepository.AuthQuery(
                limit = Limit.all,
                authIds = listOf(SecurityContext.currentAuthId)
            )
        ).filterIsInstance<Auth.Email>().first { it.hash == encodedPassword }
            ?: throw NoSuchElementException("Email not found")


        val newSalt = generateSalt()
        return AccountUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = account.id,
            hash = encodePassword(
                password = req.newPassword,
                salt = newSalt
            ),
            salt = newSalt,
            email = match.email
        ).also(requestEnqueue::invoke)
    }
}