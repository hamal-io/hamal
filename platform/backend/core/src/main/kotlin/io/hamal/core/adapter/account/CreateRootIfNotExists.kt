package io.hamal.core.adapter.account

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.component.EncodePassword
import io.hamal.core.component.GenerateSalt
import io.hamal.core.component.GenerateToken
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.request.AccountCreateRootRequest
import io.hamal.lib.domain.vo.*
import org.springframework.stereotype.Component

fun interface AccountCreateRootPort {
    operator fun invoke(req: AccountCreateRootRequest)
}

@Component
class AccountCreateRootAdapter(
    private val generateSalt: GenerateSalt,
    private val generateDomainId: GenerateDomainId,
    private val encodePassword: EncodePassword,
    private val generateToken: GenerateToken,
    private val requestEnqueue: RequestEnqueuePort,
    private val accountFind: AccountFindPort
) : AccountCreateRootPort {
    override fun invoke(req: AccountCreateRootRequest) {
        val salt = generateSalt()
        accountFind(AccountId.root)
            ?: run {
                AccountCreateRequested(
                    id = generateDomainId(::RequestId),
                    by = SecurityContext.currentAuthId,
                    status = RequestStatus.Submitted,
                    accountId = AccountId.root,
                    accountType = AccountType.Root,
                    workspaceId = WorkspaceId.root,
                    namespaceId = NamespaceId.root,
                    email = req.email,
                    emailAuthId = AuthId.root,
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

}