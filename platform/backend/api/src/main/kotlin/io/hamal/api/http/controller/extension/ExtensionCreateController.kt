package io.hamal.api.http.controller.extension

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.extension.ExtensionCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiExtensionCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionCreateController(
    private val retry: Retry,
    private val extensionCreate: ExtensionCreatePort
) {
    @PostMapping("/v1/workspaces/{workspaceId}/extensions")
    fun create(
        @PathVariable("workspaceId") workspaceId: WorkspaceId,
        @RequestBody req: ApiExtensionCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        extensionCreate(workspaceId, req).accepted()
    }

}