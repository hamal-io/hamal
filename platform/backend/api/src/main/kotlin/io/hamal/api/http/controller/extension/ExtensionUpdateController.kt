package io.hamal.api.http.controller.extension

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.ExtensionUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sdk.api.ApiExtensionUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
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
    @PatchMapping("/v1/extensions/{extId}")
    fun update(
        @PathVariable("extId") extId: ExtensionId,
        @RequestBody req: ApiExtensionUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        updateExtension(extId, req).accepted()
    }
}