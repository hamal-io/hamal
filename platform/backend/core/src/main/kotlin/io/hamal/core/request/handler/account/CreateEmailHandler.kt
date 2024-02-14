package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.vo.AuthTokenExpiresAt
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateEmailHandler(
    val accountCmdRepository: AccountCmdRepository,
    val authCmdRepository: AuthCmdRepository,
    val workspaceCmdRepository: WorkspaceCmdRepository,
    val namespaceCmdRepository: NamespaceCmdRepository,
    val eventEmitter: InternalEventEmitter
) : io.hamal.core.request.RequestHandler<AccountCreateRequested>(AccountCreateRequested::class) {

    override fun invoke(req: AccountCreateRequested) {
        createAccount(req)
            .also { emitEvent(req.cmdId(), it) }
            .also { createWorkspace(req) }
            .also { createNamespace(req) }
            .also { createEmailAuth(req) }
            .also { createTokenAuth(req) }
    }
}

private fun AccountCreateEmailHandler.createAccount(req: AccountCreateRequested): Account {
    return accountCmdRepository.create(
        AccountCmdRepository.CreateCmd(
            id = req.cmdId(),
            accountId = req.accountId,
            accountType = req.accountType,
            salt = req.salt
        )
    )
}

private fun AccountCreateEmailHandler.createWorkspace(req: AccountCreateRequested): Workspace {
    return workspaceCmdRepository.create(
        WorkspaceCmdRepository.CreateCmd(
            id = req.cmdId(),
            workspaceId = req.workspaceId,
            name = WorkspaceName("Workspace ${req.workspaceId}"),
            creatorId = req.accountId
        )
    )
}

private fun AccountCreateEmailHandler.createNamespace(req: AccountCreateRequested): Namespace {
    return namespaceCmdRepository.create(
        NamespaceCmdRepository.CreateCmd(
            id = req.cmdId(),
            namespaceId = req.namespaceId,
            workspaceId = req.workspaceId,
            name = NamespaceName.default
        )
    )
}


private fun AccountCreateEmailHandler.createEmailAuth(req: AccountCreateRequested): Auth {
    return authCmdRepository.create(
        AuthCmdRepository.CreateEmailAuthCmd(
            id = req.cmdId(),
            authId = req.passwordAuthId,
            accountId = req.accountId,
            email = req.email,
            hash = req.hash
        )
    )
}

private fun AccountCreateEmailHandler.createTokenAuth(req: AccountCreateRequested): Auth {
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

private fun AccountCreateEmailHandler.emitEvent(cmdId: CmdId, account: Account) {
    eventEmitter.emit(cmdId, AccountCreatedEvent(account))
}
