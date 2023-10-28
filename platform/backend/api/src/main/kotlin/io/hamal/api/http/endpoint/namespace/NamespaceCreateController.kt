package io.hamal.api.http.endpoint.namespace

import io.hamal.core.adapter.NamespaceCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceCreateController(
    private val retry: Retry,
    private val createNamespace: NamespaceCreatePort
) {
    @PostMapping("/v1/groups/{groupId}/namespaces")
    fun createNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiNamespaceCreateReq
    ): ResponseEntity<ApiSubmittedReqImpl<NamespaceId>> = retry {
        createNamespace(groupId, req) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = it.id,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }
}