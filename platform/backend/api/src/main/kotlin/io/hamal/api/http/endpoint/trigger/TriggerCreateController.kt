package io.hamal.api.http.endpoint.trigger

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateTriggerPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiCreateTriggerReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerCreateController(
    private val retry: Retry,
    private val createTrigger: CreateTriggerPort
) {
    @PostMapping("/v1/namespaces/{namespaceId}/triggers")
    fun createTrigger(
        @PathVariable namespaceId: NamespaceId,
        @RequestBody req: ApiCreateTriggerReq
    ): ResponseEntity<ApiSubmittedReq> {
        return retry {
            createTrigger(namespaceId, req) {
                ResponseEntity(Assembler.assemble(it), ACCEPTED)
            }
        }
    }
}
