package io.hamal.core.adapter.workspace

import io.hamal.core.adapter.security.EnsureAccessPort
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.repository.api.Workspace
import io.hamal.repository.api.WorkspaceQueryRepository
import org.springframework.stereotype.Component

fun interface WorkspaceGetPort {
    operator fun invoke(workspaceId: WorkspaceId): Workspace
}

@Component
class WorkspaceGetAdapter(
    private val workspaceQueryRepository: WorkspaceQueryRepository,
    private val ensureAccess: EnsureAccessPort
) : WorkspaceGetPort {
    override fun invoke(workspaceId: WorkspaceId): Workspace = ensureAccess(workspaceQueryRepository.get(workspaceId))
}