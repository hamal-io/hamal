package io.hamal.api.http.controller.workspace

import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiWorkspaceList
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class WorkspaceListController(
    private val workspaceList: WorkspaceListPort
) {
    @GetMapping("/v1/workspaces")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: WorkspaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<WorkspaceId>,
    ): ResponseEntity<ApiWorkspaceList> {
        return workspaceList(
            WorkspaceQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = ids
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