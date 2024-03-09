package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountCreateRequested
import io.hamal.lib.domain.vo.ExpiresAt
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateEmailHandler(
    private val accountCmdRepository: AccountCmdRepository,
    private val authCmdRepository: AuthCmdRepository,
    private val workspaceCmdRepository: WorkspaceCmdRepository,
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountCreateRequested>(AccountCreateRequested::class) {

    override fun invoke(req: AccountCreateRequested) {
        createAccount(req)
            .also { createWorkspace(req) }
            .also { createNamespace(req) }
            .also { createNamespaceTree(req) }
            .also { createEmailAuth(req) }
            .also { createTokenAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createAccount(req: AccountCreateRequested): Account {
        return accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = req.cmdId(),
                accountId = req.accountId,
                accountType = req.accountType,
                salt = req.salt
            )
        )
    }

    private fun createWorkspace(req: AccountCreateRequested): Workspace {
        return workspaceCmdRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                workspaceId = req.workspaceId,
                name = WorkspaceName("Workspace ${req.workspaceId}"),
                creatorId = req.accountId
            )
        )
    }

    private fun createNamespace(req: AccountCreateRequested): Namespace {
        return namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                namespaceId = req.namespaceId,
                workspaceId = req.workspaceId,
                name = NamespaceName.default
            )
        )
    }

    private fun createNamespaceTree(req: AccountCreateRequested): NamespaceTree {
        return namespaceTreeCmdRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = req.cmdId(),
                treeId = NamespaceTreeId(req.namespaceId.value),
                rootNodeId = req.namespaceId,
                workspaceId = req.workspaceId,
            )
        )
    }

    private fun createEmailAuth(req: AccountCreateRequested): Auth {
        return authCmdRepository.create(
            AuthCmdRepository.CreateEmailAuthCmd(
                id = req.cmdId(),
                authId = req.emailAuthId,
                accountId = req.accountId,
                email = req.email,
                hash = req.hash
            )
        )
    }

    private fun createTokenAuth(req: AccountCreateRequested): Auth {
        return authCmdRepository.create(
            AuthCmdRepository.CreateTokenAuthCmd(
                id = req.cmdId(),
                authId = req.tokenAuthId,
                accountId = req.accountId,
                token = req.token,
                expiresAt = ExpiresAt(TimeUtils.now().plus(30, ChronoUnit.DAYS))
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, account: Account) {
        eventEmitter.emit(cmdId, AccountCreatedEvent(account))
    }
}