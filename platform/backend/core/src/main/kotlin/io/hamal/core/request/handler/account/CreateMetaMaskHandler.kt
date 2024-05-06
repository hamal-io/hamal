package io.hamal.core.request.handler.account

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.RequestHandler
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.request.AccountCreateMetaMaskRequested
import io.hamal.lib.domain.vo.ExpiresAt.Companion.ExpiresAt
import io.hamal.lib.domain.vo.NamespaceFeaturesMap
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.NamespaceTreeId
import io.hamal.lib.domain.vo.WorkspaceName.Companion.WorkspaceName
import io.hamal.repository.api.*
import io.hamal.repository.api.event.AccountCreatedEvent
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit

@Component
class AccountCreateMetaMaskHandler(
    private val accountCmdRepository: AccountCmdRepository,
    private val authCmdRepository: AuthCmdRepository,
    private val workspaceCmdRepository: WorkspaceCmdRepository,
    private val namespaceCmdRepository: NamespaceCmdRepository,
    private val namespaceTreeCmdRepository: NamespaceTreeCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : RequestHandler<AccountCreateMetaMaskRequested>(AccountCreateMetaMaskRequested::class) {

    override fun invoke(req: AccountCreateMetaMaskRequested) {
        createAccount(req)
            .also { createWorkspace(req) }
            .also { createNamespace(req) }
            .also { createNamespaceTree(req) }
            .also { createMetaMaskAuth(req) }
            .also { createTokenAuth(req) }
            .also { emitEvent(req.cmdId(), it) }
    }

    private fun createAccount(req: AccountCreateMetaMaskRequested): Account {
        return accountCmdRepository.create(
            AccountCmdRepository.CreateCmd(
                id = req.cmdId(),
                accountId = req.id,
                accountType = req.type,
                salt = req.salt
            )
        )
    }

    private fun createWorkspace(req: AccountCreateMetaMaskRequested): Workspace {
        return workspaceCmdRepository.create(
            WorkspaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                workspaceId = req.workspaceId,
                name = WorkspaceName("Workspace ${req.workspaceId}"),
                creatorId = req.id
            )
        )
    }

    private fun createNamespace(req: AccountCreateMetaMaskRequested): Namespace {
        return namespaceCmdRepository.create(
            NamespaceCmdRepository.CreateCmd(
                id = req.cmdId(),
                namespaceId = req.namespaceId,
                workspaceId = req.workspaceId,
                name = NamespaceName.default,
                features = NamespaceFeaturesMap.default
            )
        )
    }

    private fun createNamespaceTree(req: AccountCreateMetaMaskRequested): NamespaceTree {
        return namespaceTreeCmdRepository.create(
            NamespaceTreeCmdRepository.CreateCmd(
                id = req.cmdId(),
                treeId = NamespaceTreeId(req.namespaceId.value),
                rootNodeId = req.namespaceId,
                workspaceId = req.workspaceId,
            )
        )
    }


    private fun createMetaMaskAuth(req: AccountCreateMetaMaskRequested): Auth {
        return authCmdRepository.create(
            AuthCmdRepository.CreateMetaMaskAuthCmd(
                id = req.cmdId(),
                authId = req.metamaskAuthId,
                accountId = req.id,
                address = req.address
            )
        )
    }

    private fun createTokenAuth(req: AccountCreateMetaMaskRequested): Auth {
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