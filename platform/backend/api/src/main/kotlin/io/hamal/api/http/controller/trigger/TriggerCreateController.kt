package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TriggerCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.sdk.api.ApiTriggerCreateReq
import io.hamal.repository.api.submitted_req.TriggerCreateSubmitted
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
    ): ResponseEntity<ApiSubmitted> {
        return retry {
            createTrigger(namespaceId, req, TriggerCreateSubmitted::accepted)
        }
    }
}
