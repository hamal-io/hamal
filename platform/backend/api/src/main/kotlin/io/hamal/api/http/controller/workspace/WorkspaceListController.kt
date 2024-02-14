package io.hamal.api.http.controller.workspace

import io.hamal.api.http.auth.AuthContextHolder
import io.hamal.core.adapter.WorkspaceListPort
import io.hamal.lib.sdk.api.ApiWorkspaceList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class WorkspaceListController(
    private val list: WorkspaceListPort
) {
    @GetMapping("/v1/workspaces")
    fun listWorkspace(): ResponseEntity<ApiWorkspaceList> {
        return list(AuthContextHolder.get().accountId) { workspaces ->
            ResponseEntity.ok(
                ApiWorkspaceList(
                    workspaces = workspaces.map { workspace ->
                        ApiWorkspaceList.Workspace(
                            id = workspace.id,
                            name = workspace.name
                        )
                    }
                )
            )
        }
    }
}