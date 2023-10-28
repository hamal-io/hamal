package io.hamal.api.http.endpoint.extension

import io.hamal.core.adapter.ExtensionUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sdk.api.ApiExtensionUpdateReq
import io.hamal.lib.sdk.api.ApiSubmittedReqImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionUpdateController(
    private val retry: Retry, private val updateExtension: ExtensionUpdatePort
) {
    @PatchMapping("/v1/extensions/{extId}/update")
    fun updateExtension(
        @PathVariable("extId") extId: ExtensionId, @RequestBody req: ApiExtensionUpdateReq
    ): ResponseEntity<ApiSubmittedReqImpl<ExtensionId>> = retry {
        updateExtension(extId, req) {
            ResponseEntity.accepted().body(
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