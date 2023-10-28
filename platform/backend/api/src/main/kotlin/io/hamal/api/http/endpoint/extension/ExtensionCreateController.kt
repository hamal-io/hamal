package io.hamal.api.http.endpoint.extension

import io.hamal.core.adapter.ExtensionExtensionPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiExtensionCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
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
        @RequestBody req: ApiExtensionCreateReq
    ): ResponseEntity<ApiSubmittedReqImpl<ExtensionId>> = retry {
        createExtension(groupId, req) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = null,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }

}