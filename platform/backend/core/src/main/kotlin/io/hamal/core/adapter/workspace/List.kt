package io.hamal.core.adapter.workspace

import io.hamal.core.adapter.security.FilterAccessPort
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.stereotype.Component

fun interface WorkspaceListPort {
    operator fun invoke(query: WorkspaceQuery): List<Workspace>
}

@Component
class WorkspaceListAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository,
    private val filterAccess: FilterAccessPort
) : WorkspaceListPort {
    override fun invoke(query: WorkspaceQuery): List<Workspace> = filterAccess(workspaceQueryRepository.list(query))
}