package io.hamal.core.req.handler.account

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import io.hamal.repository.api.submitted_req.AccountCreateSubmitted
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateWithPasswordHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val flowCmdRepository: FlowCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<AccountCreateSubmitted>(AccountCreateSubmitted::class) {

    override fun invoke(req: AccountCreateSubmitted) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
            .also { createFlow(req) }
            .also { createPasswordAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun AccountCreateWithPasswordHandler.createAccount(req: AccountCreateSubmitted): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.accountId,
            accountType = req.type,
            name = req.name,
            email = req.email,
            salt = req.salt
        )
    )
}

private fun AccountCreateWithPasswordHandler.createGroup(req: AccountCreateSubmitted): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("Group of ${req.name.value}"),
            creatorId = req.accountId
        )
    )
}

private fun AccountCreateWithPasswordHandler.createFlow(req: AccountCreateSubmitted): Flow {
    return flowCmdRepository.create(
        FlowCmdRepository.CreateCmd(
            id = req.cmdId(),
            flowId = req.flowId,
            groupId = req.groupId,
            type = FlowType("__default__"),
            name = FlowName("__default__"),
            inputs = FlowInputs()
        )
    )
}


private fun AccountCreateWithPasswordHandler.createPasswordAuth(req: AccountCreateSubmitted): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreatePasswordAuthCmd(
            id = req.cmdId(),
            authId = req.passwordAuthId,
            accountId = req.accountId,
            hash = req.hash
        )
    )
}

private fun AccountCreateWithPasswordHandler.createTokenAuth(req: AccountCreateSubmitted): Auth {
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

private fun AccountCreateWithPasswordHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountCreatedEvent(account))
}