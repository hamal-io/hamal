package io.hamal.core.adapter

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component


interface WorkspaceGetPort {
    operator fun invoke(workspaceId: WorkspaceId): Workspace
}

interface WorkspaceListPort {
    operator fun invoke(query: WorkspaceQuery): (List<Workspace>)
    operator fun invoke(accountId: AccountId): (List<Workspace>)
}

interface WorkspacePort : WorkspaceGetPort, WorkspaceListPort

@Component
class WorkspaceAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository
) : WorkspacePort {

    override operator fun invoke(workspaceId: WorkspaceId): Workspace = workspaceQueryRepository.get(workspaceId)

    override operator fun invoke(query: WorkspaceQuery): (List<Workspace>) = workspaceQueryRepository.list(query)

    override fun invoke(accountId: AccountId): List<Workspace> {
        // FIXME this must come directly from the repository
        return workspaceQueryRepository.list(
            WorkspaceQuery(
                limit = Limit.all
            )
        ).filter { it.creatorId == accountId }
    }
}