package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.trigger.TriggerCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerCreateController(
    private val retry: Retry,
    private val triggerCreate: TriggerCreatePort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/triggers")
    fun create(
        @PathVariable namespaceId: NamespaceId,
        @RequestBody req: ApiTriggerCreateReq
    ): ResponseEntity<ApiRequested> {
        return retry {
            triggerCreate(namespaceId, req).accepted()
        }
    }
}
