package io.hamal.api.http.endpoint.extension

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateExtensionPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCreateExtensionReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionCreateController(
    private val retry: Retry,
    private val createExtension: CreateExtensionPort
) {
    @PostMapping("/v1/groups/{groupId}/extensions")
    fun createExtension(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiCreateExtensionReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createExtension(groupId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }

}