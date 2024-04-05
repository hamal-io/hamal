package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.AccountPasswordChangeRequested
import io.hamal.repository.api.*
import io.hamal.repository.api.AccountCmdRepository.UpdateCmd
import io.hamal.repository.api.event.AccountUpdatedEvent
import org.springframework.stereotype.Component

@Component
class AccountUpdateHandler(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountPasswordChangeRequested>(AccountPasswordChangeRequested::class) {

    override fun invoke(req: AccountPasswordChangeRequested) {
        updateAccount(req)
            .also { createEmailAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateAccount(req: AccountPasswordChangeRequested): Account {
        return accountRepository.update(
            req.id, UpdateCmd(
                id = req.cmdId(),
                salt = req.salt
            )
        ).also {
            authRepository.revokeAuth(
                AuthCmdRepository.RevokeAuthCmd(req.cmdId(), req.requestedBy)
            )
        }
    }

    private fun createEmailAuth(req: AccountPasswordChangeRequested): Auth {
        return authRepository.create(
            AuthCmdRepository.CreateEmailAuthCmd(
                id = req.cmdId(),
                authId = req.requestedBy,
                accountId = req.id,
                email = req.email,
                hash = req.hash
            )
        )
    }


    private fun emitEvent(cmdId: CmdId, account: Account) {
        eventEmitter.emit(cmdId, AccountUpdatedEvent(account))
    }
}