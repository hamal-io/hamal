package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.trigger.TriggerSetStatusPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain._enum.TriggerStates.Active
import io.hamal.lib.domain._enum.TriggerStates.Inactive
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerStatus.Companion.TriggerStatus
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
    fun activate(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiRequested> = retry {
        triggerSetStatus(triggerId, TriggerStatus(Active)).accepted()
    }


    @PostMapping("/v1/trigger/{triggerId}/deactivate")
    fun deactivate(
        @PathVariable triggerId: TriggerId
    ): ResponseEntity<ApiRequested> = retry {
        triggerSetStatus(triggerId, TriggerStatus(Inactive)).accepted()
    }
}
