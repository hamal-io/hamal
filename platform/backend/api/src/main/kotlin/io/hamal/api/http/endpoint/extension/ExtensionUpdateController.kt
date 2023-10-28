package io.hamal.api.http.endpoint.extension

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.ExtensionUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sdk.api.ApiExtensionUpdateReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionUpdateController(
    private val retry: Retry,
    private val updateExtension: ExtensionUpdatePort
) {
    @PatchMapping("/v1/extensions/{extId}/update")
    fun updateExtension(
        @PathVariable("extId") extId: ExtensionId,
        @RequestBody req: ApiExtensionUpdateReq
    ) = retry {
        updateExtension(extId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
    }
}