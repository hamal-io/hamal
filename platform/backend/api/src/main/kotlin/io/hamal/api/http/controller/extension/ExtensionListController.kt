package io.hamal.api.http.controller.extension

import io.hamal.core.adapter.extension.ExtensionListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiExtensionList
import io.hamal.repository.api.ExtensionQueryRepository.ExtensionQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionListController(private val extensionList: ExtensionListPort) {
    @GetMapping("/v1/extensions")
    fun listExtensions(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExtensionId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<ExtensionId>,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>
    ): ResponseEntity<ApiExtensionList> {
        return extensionList(
            ExtensionQuery(
                afterId = afterId,
                limit = limit,
                extensionIds = ids,
                workspaceIds = workspaceIds
            )
        ).let { extensions ->
            ResponseEntity.ok(
                ApiExtensionList(
                    extensions.map { extension ->
                        ApiExtensionList.Extension(
                            id = extension.id,
                            name = extension.name
                        )
                    })
            )
        }
    }
}