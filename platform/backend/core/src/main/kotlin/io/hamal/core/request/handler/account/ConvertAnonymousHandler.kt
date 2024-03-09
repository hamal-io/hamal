package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountConvertRequested
import io.hamal.lib.domain.vo.ExpiresAt
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.event.AccountConvertedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit


@Component
class AccountConvertAnonymousHandler(
    private val accountCmdRepository: AccountCmdRepository,
    private val authCmdRepository: AuthCmdRepository,
    private val eventEmitter: InternalEventEmitter,
) : RequestHandler<AccountConvertRequested>(AccountConvertRequested::class) {

    override fun invoke(req: AccountConvertRequested) {
        convertAccount(req)
            .also { createEmailAuth(req) }
            .also { createTokenAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun convertAccount(req: AccountConvertRequested): Account {
        return accountCmdRepository.convert(
            AccountCmdRepository.ConvertCmd(
                id = req.cmdId(),
                accountId = req.id,
                email = req.email
            )
        )
    }


    private fun createEmailAuth(req: AccountConvertRequested): Auth {
        return authCmdRepository.create(
            AuthCmdRepository.CreateEmailAuthCmd(
                id = req.cmdId(),
                authId = req.emailAuthId,
                accountId = req.id,
                email = req.email,
                hash = req.hash
            )
        )
    }

    private fun createTokenAuth(req: AccountConvertRequested): Auth {
        return authCmdRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = req.cmdId(),
                authId = req.tokenAuthId,
                accountId = req.id,
                token = req.token,
                expiresAt = ExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, account: Account) {
        eventEmitter.emit(cmdId, AccountConvertedEvent(account))
    }
}