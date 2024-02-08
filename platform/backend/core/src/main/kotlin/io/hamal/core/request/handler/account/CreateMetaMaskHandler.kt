package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountCreateMetaMaskRequested
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.lib.domain.vo.GroupName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateMetaMaskHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: InternalEventEmitter
) : io.hamal.core.request.RequestHandler<AccountCreateMetaMaskRequested>(AccountCreateMetaMaskRequested::class) {

    override fun invoke(req: AccountCreateMetaMaskRequested) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
            .also { createNamespace(req) }
            .also { createMetaMaskAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun AccountCreateMetaMaskHandler.createAccount(req: AccountCreateMetaMaskRequested): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.accountId,
            accountType = req.accountType,
            salt = req.salt
        )
    )
}

private fun AccountCreateMetaMaskHandler.createGroup(req: AccountCreateMetaMaskRequested): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("Group ${req.groupId}"),
            creatorId = req.accountId
        )
    )
}

private fun AccountCreateMetaMaskHandler.createNamespace(req: AccountCreateMetaMaskRequested): Namespace {
    return namespaceCmdRepository.create(
        NamespaceCmdRepository.CreateCmd(
            id = req.cmdId(),
            namespaceId = req.namespaceId,
            groupId = req.groupId,
            name = NamespaceName.default
        )
    )
}


private fun AccountCreateMetaMaskHandler.createMetaMaskAuth(req: AccountCreateMetaMaskRequested): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreateMetaMaskAuthCmd(
            id = req.cmdId(),
            authId = req.metamaskAuthId,
            accountId = req.accountId,
            address = req.address
        )
    )
}

private fun AccountCreateMetaMaskHandler.createTokenAuth(req: AccountCreateMetaMaskRequested): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreateTokenAuthCmd(
            id = req.cmdId(),
            authId = req.tokenAuthId,
            accountId = req.accountId,
            token = req.token,
            expiresAt = AuthTokenExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
        )
    )
}

private fun AccountCreateMetaMaskHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountCreatedEvent(account))
}
