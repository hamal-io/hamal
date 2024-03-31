package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.AccountUpdateRequested
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository.UpdateCmd
import io.hamal.repository.api.AccountRepository
import io.hamal.repository.api.event.AccountUpdatedEvent
import org.springframework.stereotype.Component

@Component
class AccountUpdateHandler(
    private val accountRepository: AccountRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountUpdateRequested>(AccountUpdateRequested::class) {

    override fun invoke(req: AccountUpdateRequested) {
        updateAccount(req)
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateAccount(req: AccountUpdateRequested): Account {
        return accountRepository.update(
            req.id, UpdateCmd(
                id = req.cmdId(),
                email = req.email,
                password = req.salt
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, account: Account) {
        eventEmitter.emit(cmdId, AccountUpdatedEvent(account))
    }
}