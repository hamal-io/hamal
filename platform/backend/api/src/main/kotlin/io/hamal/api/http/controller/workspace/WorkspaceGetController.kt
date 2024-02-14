package io.hamal.api.http.controller.workspace

import io.hamal.core.adapter.WorkspaceGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiWorkspace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class WorkspaceGetController(
    private val retry: Retry,
    private val getWorkspace: WorkspaceGetPort
) {
    @GetMapping("/v1/workspaces/{workspaceId}")
    fun getWorkspace(
        @PathVariable("workspaceId") workspaceId: WorkspaceId,
    ): ResponseEntity<ApiWorkspace> {
        return retry {
            getWorkspace(workspaceId) { workspace ->
                ResponseEntity.ok(
                    ApiWorkspace(
                        id = workspace.id,
                        name = workspace.name,
                    )
                )
            }
        }
    }
}