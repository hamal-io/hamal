package io.hamal.core.adapter.security

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.NamespaceTreeQueryRepository
import io.hamal.repository.api.WorkspaceQueryRepository

internal abstract class BaseAccess(
    private val workspaceQueryRepository: WorkspaceQueryRepository,
    private val namespaceTreeQueryRepository: NamespaceTreeQueryRepository
) {

    internal fun accessAllowed(auth: Auth.Account, accountId: AccountId): Boolean {
        return auth.accountId == accountId
    }

    internal fun accessAllowed(auth: Auth.Account, workspaceId: WorkspaceId): Boolean {
        return workspaceQueryRepository.list(
            WorkspaceQueryRepository.WorkspaceQuery(
                limit = Limit.all,
                accountIds = listOf(auth.accountId)
            )
        ).any { it.workspaceId == workspaceId }
    }

    internal fun accessAllowed(auth: Auth.Account, namespaceId: NamespaceId): Boolean {
        return namespaceTreeQueryRepository.list(
            NamespaceTreeQueryRepository.NamespaceTreeQuery(
                limit = Limit.one,
                workspaceIds = workspaceQueryRepository.list(
                    WorkspaceQueryRepository.WorkspaceQuery(
                        limit = Limit.all,
                        accountIds = listOf(auth.accountId)
                    )
                ).map { it.workspaceId }
            )).any { it.root.find { node -> node.value == namespaceId } != null }
    }

}