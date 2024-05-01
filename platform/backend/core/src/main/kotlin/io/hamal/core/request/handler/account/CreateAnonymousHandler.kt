package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountCreateAnonymousRequested
import io.hamal.lib.domain.vo.ExpiresAt
import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceName.Companion.WorkspaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateAnonymousHandler(
    private val accountCmdRepository: AccountCmdRepository,
    private val authCmdRepository: AuthCmdRepository,
    private val workspaceCmdRepository: WorkspaceCmdRepository,
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountCreateAnonymousRequested>(AccountCreateAnonymousRequested::class) {

    override fun invoke(req: AccountCreateAnonymousRequested) {
        createAccount(req)
            .also { createWorkspace(req) }
            .also { createNamespace(req) }
            .also { createNamespaceTree(req) }
            .also { createTokenAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createAccount(req: AccountCreateAnonymousRequested): Account {
        return accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = req.cmdId(),
                accountId = req.id,
                accountType = req.type,
                salt = req.salt
            )
        )
    }

    private fun createWorkspace(req: AccountCreateAnonymousRequested): Workspace {
        return workspaceCmdRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                workspaceId = req.workspaceId,
                name = WorkspaceName("Workspace ${req.workspaceId.stringValue}"),
                creatorId = req.id
            )
        )
    }

    private fun createNamespace(req: AccountCreateAnonymousRequested): Namespace {
        return namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                namespaceId = req.namespaceId,
                workspaceId = req.workspaceId,
                name = NamespaceName.default,
                features = NamespaceFeatures.default
            )
        )
    }

    private fun createNamespaceTree(req: AccountCreateAnonymousRequested): NamespaceTree {
        return namespaceTreeCmdRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = req.cmdId(),
                treeId = NamespaceTreeId(req.namespaceId.value),
                rootNodeId = req.namespaceId,
                workspaceId = req.workspaceId,
            )
        )
    }

    private fun createTokenAuth(req: AccountCreateAnonymousRequested): Auth {
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
        eventEmitter.emit(cmdId, AccountCreatedEvent(account))
    }
}