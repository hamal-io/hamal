package io.hamal.api.http.controller.blueprint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.BlueprintUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sdk.api.ApiBlueprintUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.lib.domain.request.BlueprintUpdateRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class BlueprintUpdateController(
    private val retry: Retry,
    private val updateBlueprint: BlueprintUpdatePort
) {
    @PatchMapping("/v1/blueprints/{bpId}")
    fun updateBlueprint(
        @PathVariable("bpId") bpId: BlueprintId,
        @RequestBody req: ApiBlueprintUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        updateBlueprint(bpId, req, BlueprintUpdateRequested::accepted)
    }
}