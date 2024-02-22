package io.hamal.api.http.controller.workspace

import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.sdk.api.ApiWorkspaceList
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class WorkspaceListController(
    private val workspaceList: WorkspaceListPort
) {
    @GetMapping("/v1/workspaces")
    fun list(): ResponseEntity<ApiWorkspaceList> {
        return workspaceList(
            WorkspaceQuery(
                limit = Limit.all,
                accountId = listOf(SecurityContext.currentAccountId)
            )
        ).let { workspaces ->
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