package io.hamal.api.http.endpoint.hook

import io.hamal.core.adapter.HookCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiHookCreateReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookCreateController(
    private val retry: Retry,
    private val createHook: HookCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/hooks")
    fun createHook(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody req: ApiHookCreateReq
    ): ResponseEntity<ApiSubmittedReqImpl<HookId>> = retry {
        createHook(namespaceId, req) {
            ResponseEntity
                .accepted()
                .body(
                    ApiSubmittedReqImpl(
                        reqId = it.reqId,
                        status = it.status,
                        namespaceId = it.namespaceId,
                        groupId = it.groupId,
                        id = it.id
                    )
                )
        }
    }
}