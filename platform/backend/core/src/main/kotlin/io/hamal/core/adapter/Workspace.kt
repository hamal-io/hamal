package io.hamal.core.adapter

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component


interface WorkspaceGetPort {
    operator fun <T : Any> invoke(workspaceId: WorkspaceId, responseHandler: (Workspace) -> T): T
}

interface WorkspaceListPort {
    operator fun <T : Any> invoke(query: WorkspaceQuery, responseHandler: (List<Workspace>) -> T): T
    operator fun <T : Any> invoke(accountId: AccountId, responseHandler: (List<Workspace>) -> T): T
}

interface WorkspacePort : WorkspaceGetPort, WorkspaceListPort

@Component
class WorkspaceAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository
) : WorkspacePort {

    override operator fun <T : Any> invoke(
        workspaceId: WorkspaceId,
        responseHandler: (Workspace) -> T
    ): T = responseHandler(workspaceQueryRepository.get(workspaceId))

    override operator fun <T : Any> invoke(
        query: WorkspaceQuery,
        responseHandler: (List<Workspace>) -> T
    ): T = responseHandler(workspaceQueryRepository.list(query))

    override fun <T : Any> invoke(accountId: AccountId, responseHandler: (List<Workspace>) -> T): T {
        // FIXME this must come directly from the repository
        return responseHandler(workspaceQueryRepository.list(
            WorkspaceQuery(
                limit = Limit.all
            )
        ).filter { it.creatorId == accountId }
        )
    }
}