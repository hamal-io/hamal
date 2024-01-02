package io.hamal.core.req.handler.account

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.submitted.AccountCreateMetaMaskSubmitted
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateMetaMaskHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val flowCmdRepository: FlowCmdRepository,
    val eventEmitter: PlatformEventEmitter
) : ReqHandler<AccountCreateMetaMaskSubmitted>(AccountCreateMetaMaskSubmitted::class) {

    override fun invoke(req: AccountCreateMetaMaskSubmitted) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
            .also { createFlow(req) }
            .also { createMetaMaskAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun AccountCreateMetaMaskHandler.createAccount(req: AccountCreateMetaMaskSubmitted): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.accountId,
            accountType = req.type,
            salt = req.salt
        )
    )
}

private fun AccountCreateMetaMaskHandler.createGroup(req: AccountCreateMetaMaskSubmitted): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("Group ${req.groupId}"),
            creatorId = req.accountId
        )
    )
}

private fun AccountCreateMetaMaskHandler.createFlow(req: AccountCreateMetaMaskSubmitted): Flow {
    return flowCmdRepository.create(
        FlowCmdRepository.CreateCmd(
            id = req.cmdId(),
            flowId = req.flowId,
            groupId = req.groupId,
            type = FlowType.default,
            name = FlowName.default,
            inputs = FlowInputs()
        )
    )
}


private fun AccountCreateMetaMaskHandler.createMetaMaskAuth(req: AccountCreateMetaMaskSubmitted): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreateMetaMaskAuthCmd(
            id = req.cmdId(),
            authId = req.metamaskAuthId,
            accountId = req.accountId,
            address = req.address
        )
    )
}

private fun AccountCreateMetaMaskHandler.createTokenAuth(req: AccountCreateMetaMaskSubmitted): Auth {
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
