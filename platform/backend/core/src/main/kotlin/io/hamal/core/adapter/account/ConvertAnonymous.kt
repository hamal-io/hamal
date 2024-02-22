package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountConvertAnonymousRequest
import io.hamal.lib.domain.request.AccountConvertRequested
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface AccountConvertAnonymousPort {
    operator fun invoke(accountId: AccountId, req: AccountConvertAnonymousRequest): AccountConvertRequested
}

@Component
class AccountConvertAnonymousAdapter(
    private val accountGet: AccountGetPort,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort
) : AccountConvertAnonymousPort {
    override fun invoke(accountId: AccountId, req: AccountConvertAnonymousRequest): AccountConvertRequested {
        val account = accountGet(accountId)
        return AccountConvertRequested(
            id = generateDomainId(::RequestId),
            by = SecurityContext.currentAuthId,
            status = RequestStatus.Submitted,
            accountId = accountId,
            email = req.email,
            passwordAuthId = generateDomainId(::AuthId),
            tokenAuthId = generateDomainId(::AuthId),
            hash = encodePassword(
                password = req.password,
                salt = account.salt
            ),
            token = generateToken()
        ).also(requestEnqueue::invoke)
    }
}

