package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TriggerStatusPort
import io.hamal.lib.domain._enum.TriggerStatus
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.TriggerStatusSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerStatusController(
    private val set: TriggerStatusPort
) {
    @PostMapping("/v1/trigger/{triggerId}/activate")
    fun toggleOn(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiSubmitted> = set(triggerId, TriggerStatus.Active, TriggerStatusSubmitted::accepted)


    @PostMapping("/v1/trigger/{triggerId}/deactivate")
    fun toggleOff(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiSubmitted> = set(triggerId, TriggerStatus.Inactive, TriggerStatusSubmitted::accepted)
}
