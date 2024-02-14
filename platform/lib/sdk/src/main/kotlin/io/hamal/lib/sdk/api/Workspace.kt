package io.hamal.lib.sdk.api

import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.WorkspaceName
import io.hamal.lib.http.HttpTemplate

data class ApiWorkspaceList(
    val workspaces: List<Workspace>
) : ApiObject() {
    data class Workspace(
        val id: WorkspaceId,
        val name: WorkspaceName
    )
}

data class ApiWorkspace(
    val id: WorkspaceId,
    val name: WorkspaceName,
) : ApiObject()

interface ApiWorkspaceService

internal class ApiWorkspaceServiceImpl(
    private val template: HttpTemplate
) : ApiWorkspaceService