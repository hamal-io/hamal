package io.hamal.api.http.endpoint.trigger

import io.hamal.core.adapter.TriggerCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerCreateController(
    private val retry: Retry,
    private val createTrigger: TriggerCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/triggers")
    fun createTrigger(
        @PathVariable namespaceId: NamespaceId,
        @RequestBody req: ApiTriggerCreateReq
    ): ResponseEntity<ApiSubmittedReqImpl<TriggerId>> {
        return retry {
            createTrigger(namespaceId, req) {
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
}
