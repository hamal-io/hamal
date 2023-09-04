package io.hamal.backend.req.handler.account

import io.hamal.backend.event.HubEventEmitter
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.lib.domain.vo.GroupName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedCreateAccountWithPasswordReq
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class CreateAccountWithPasswordHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val eventEmitter: HubEventEmitter,
) : ReqHandler<SubmittedCreateAccountWithPasswordReq>(SubmittedCreateAccountWithPasswordReq::class) {

    override fun invoke(req: SubmittedCreateAccountWithPasswordReq) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
            .also { createPasswordAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun CreateAccountWithPasswordHandler.createAccount(req: SubmittedCreateAccountWithPasswordReq): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.id,
            name = req.name,
            email = req.email,
            salt = req.salt
        )
    )
}

private fun CreateAccountWithPasswordHandler.createGroup(req: SubmittedCreateAccountWithPasswordReq): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("${req.name.value}'s Group"),
            creatorId = req.id
        )
    )
}

private fun CreateAccountWithPasswordHandler.createPasswordAuth(req: SubmittedCreateAccountWithPasswordReq): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreatePasswordAuthCmd(
            id = req.cmdId(),
            authId = req.authenticationId,
            accountId = req.id,
            hash = req.hash
        )
    )
}

private fun CreateAccountWithPasswordHandler.createTokenAuth(req: SubmittedCreateAccountWithPasswordReq): Auth {
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