package io.hamal.core.req.handler.account

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import io.hamal.repository.api.submitted_req.AccountCreateSubmitted
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class CreateAccountWithPasswordHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<AccountCreateSubmitted>(AccountCreateSubmitted::class) {

    override fun invoke(req: AccountCreateSubmitted) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
            .also { createNamespace(req) }
            .also { createPasswordAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun CreateAccountWithPasswordHandler.createAccount(req: AccountCreateSubmitted): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.id,
            accountType = req.type,
            name = req.name,
            email = req.email,
            salt = req.salt
        )
    )
}

private fun CreateAccountWithPasswordHandler.createGroup(req: AccountCreateSubmitted): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("${req.name.value}'s Group"),
            creatorId = req.id
        )
    )
}

private fun CreateAccountWithPasswordHandler.createNamespace(req: AccountCreateSubmitted): Namespace {
    return namespaceCmdRepository.create(
        NamespaceCmdRepository.CreateCmd(
            id = req.cmdId(),
            namespaceId = req.namespaceId,
            groupId = req.groupId,
            name = NamespaceName("root"),
            inputs = NamespaceInputs()
        )
    )
}


private fun CreateAccountWithPasswordHandler.createPasswordAuth(req: AccountCreateSubmitted): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreatePasswordAuthCmd(
            id = req.cmdId(),
            authId = req.authenticationId,
            accountId = req.id,
            hash = req.hash
        )
    )
}

private fun CreateAccountWithPasswordHandler.createTokenAuth(req: AccountCreateSubmitted): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreateTokenAuthCmd(
            id = req.cmdId(),
            authId = req.authenticationId,
            accountId = req.id,
            token = req.token,
            expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
        )
    )
}

private fun CreateAccountWithPasswordHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountCreatedEvent(account))
}
