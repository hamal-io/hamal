package io.hamal.backend.req.handler.account

import io.hamal.backend.event.HubEventEmitter
import io.hamal.backend.req.ReqHandler
import io.hamal.backend.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.GroupName
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.Group
import io.hamal.repository.api.GroupCmdRepository
import io.hamal.repository.api.event.AccountCreatedEvent
import io.hamal.repository.api.submitted_req.SubmittedCreateAccountWithPasswordReq
import org.springframework.stereotype.Component

@Component
class CreateAccountHandler(
    val accountCmdRepository: AccountCmdRepository,
//    val authenticationCmdRepository: AuthenticationCmdRepository,
    val groupCmdRepository: GroupCmdRepository,
    val eventEmitter: HubEventEmitter,
) : ReqHandler<SubmittedCreateAccountWithPasswordReq>(SubmittedCreateAccountWithPasswordReq::class) {
    override fun invoke(req: SubmittedCreateAccountWithPasswordReq) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createGroup(req) }
    }
}

private fun CreateAccountHandler.createAccount(req: SubmittedCreateAccountWithPasswordReq): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.id,
            name = req.name,
            email = req.email
        )
    )
}

private fun CreateAccountHandler.createGroup(req: SubmittedCreateAccountWithPasswordReq): Group {
    return groupCmdRepository.create(
        GroupCmdRepository.CreateCmd(
            id = req.cmdId(),
            groupId = req.groupId,
            name = GroupName("${req.name.value}'s Group"),
            creatorId = req.id
        )
    )
}

private fun CreateAccountHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountCreatedEvent(account))
}
