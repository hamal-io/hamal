package io.hamal.core.req.handler.account

import io.hamal.core.event.PlatformEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.repository.api.Account
import io.hamal.repository.api.AccountCmdRepository
import io.hamal.repository.api.Auth
import io.hamal.repository.api.AuthCmdRepository
import io.hamal.repository.api.event.AccountConvertedEvent
import io.hamal.repository.api.submitted_req.AccountConvertSubmitted
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit


@Component
class AccountConvertAnonymousHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val eventEmitter: PlatformEventEmitter,
) : ReqHandler<AccountConvertSubmitted>(AccountConvertSubmitted::class) {

    override fun invoke(req: AccountConvertSubmitted) {
        convertAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createPasswordAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun AccountConvertAnonymousHandler.convertAccount(req: AccountConvertSubmitted): Account {
    return accountCmdRepository.convert(
        AccountCmdRepository.ConvertCmd(
            id = req.cmdId(),
            accountId = req.accountId,
            name = req.name,
            email = req.email,
        )
    )
}


private fun AccountConvertAnonymousHandler.createPasswordAuth(req: AccountConvertSubmitted): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreatePasswordAuthCmd(
            id = req.cmdId(),
            authId = req.passwordAuthId,
            accountId = req.accountId,
            hash = req.hash
        )
    )
}

private fun AccountConvertAnonymousHandler.createTokenAuth(req: AccountConvertSubmitted): Auth {
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

private fun AccountConvertAnonymousHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountConvertedEvent(account))
}
