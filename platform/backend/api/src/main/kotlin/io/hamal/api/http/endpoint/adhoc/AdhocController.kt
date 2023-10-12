package io.hamal.api.http.endpoint.adhoc

import io.hamal.api.http.endpoint.req.Assembler.assemble
import io.hamal.core.adapter.InvokeAdhocPort
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiInvokeAdhocReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(private val invokeAdhoc: InvokeAdhocPort) {
    @PostMapping("/v1/groups/{groupId}/adhoc")
    fun groupAdhoc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiInvokeAdhocReq
    ): ResponseEntity<ApiSubmittedReq> =
        invokeAdhoc(groupId, req) {
            ResponseEntity(assemble(it), ACCEPTED)
        }
}
