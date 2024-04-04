package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountUpdateRequest
import io.hamal.lib.domain.request.AccountUpdateRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface AccountUpdatePort {
    operator fun invoke(accountId: AccountId, req: AccountUpdateRequest): AccountUpdateRequested
}

@Component
class AccountUpdateAdapter(
    private val accountGet: AccountGetPort,
    private val encodePassword: EncodePassword,
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : AccountUpdatePort {
    override fun invoke(accountId: AccountId, req: AccountUpdateRequest): AccountUpdateRequested {
        val salt = generateSalt()
        val account = accountGet(accountId)
        return AccountUpdateRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus.Submitted,
            id = account.id,
            hash = encodePassword(
                password = req.password,
                salt = salt
            ),
            salt = salt
        ).also(requestEnqueue::invoke)
    }

}