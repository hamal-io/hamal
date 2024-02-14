package io.hamal.repository.api.event

import io.hamal.repository.api.Workspace

data class WorkspaceCreatedEvent(
    val workspace: Workspace,
) : InternalEvent()
