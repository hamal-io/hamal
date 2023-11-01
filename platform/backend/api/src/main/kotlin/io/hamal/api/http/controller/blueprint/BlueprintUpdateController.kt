package io.hamal.api.http.controller.blueprint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.BlueprintUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sdk.api.ApiBlueprintUpdateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.BlueprintUpdateSubmitted
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
        @RequestBody req: ApiBlueprintUpdateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        updateBlueprint(bpId, req, BlueprintUpdateSubmitted::accepted)
    }
}