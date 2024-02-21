package io.hamal.core.adapter.workspace

import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component

fun interface WorkspaceListPort {
    operator fun invoke(query: WorkspaceQuery): List<Workspace>
}

@Component
class WorkspaceListAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository
) : WorkspaceListPort {
    override fun invoke(query: WorkspaceQuery): List<Workspace> = workspaceQueryRepository.list(query)
}