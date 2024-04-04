package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.request.AccountUpdateRequested
import io.hamal.repository.api.*
import io.hamal.repository.api.AccountCmdRepository.PasswordChangeCmd
import io.hamal.repository.api.event.AccountUpdatedEvent
import org.springframework.stereotype.Component

@Component
class AccountUpdateHandler(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountUpdateRequested>(AccountUpdateRequested::class) {

    override fun invoke(req: AccountUpdateRequested) {
        updateAccount(req)
            .also { createEmailAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun updateAccount(req: AccountUpdateRequested): Account {
        return accountRepository.changePassword(
            req.id, PasswordChangeCmd(
                id = req.cmdId(),
                salt = req.salt
            )
        ).also {
            authRepository.revokeAuth(
                AuthCmdRepository.RevokeAuthCmd(req.cmdId(), req.requestedBy)
            )
        }
    }

    private fun createEmailAuth(req: AccountUpdateRequested): Auth {
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