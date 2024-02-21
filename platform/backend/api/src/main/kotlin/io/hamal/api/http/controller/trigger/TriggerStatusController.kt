package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.TriggerSetStatusPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain._enum.TriggerStatus.Active
import io.hamal.lib.domain._enum.TriggerStatus.Inactive
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerStatusController(
    private val triggerSetStatus: TriggerSetStatusPort,
    private val retry: Retry
) {
    @PostMapping("/v1/trigger/{triggerId}/activate")
    fun toggleOn(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiRequested> = retry {
        triggerSetStatus(triggerId, Active).accepted()
    }


    @PostMapping("/v1/trigger/{triggerId}/deactivate")
    fun toggleOff(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiRequested> = retry {
        triggerSetStatus(triggerId, Inactive).accepted()
    }
}
