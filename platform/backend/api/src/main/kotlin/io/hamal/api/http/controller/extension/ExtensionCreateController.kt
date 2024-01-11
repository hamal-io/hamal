package io.hamal.api.http.controller.extension

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.ExtensionExtensionPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.request.ExtensionCreateRequested
import io.hamal.lib.domain.vo.GroupId
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
    private val createExtension: ExtensionExtensionPort
) {
    @PostMapping("/v1/groups/{groupId}/extensions")
    fun createExtension(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiExtensionCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        createExtension(groupId, req, ExtensionCreateRequested::accepted)
    }

}