package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.trigger.TriggerDeletePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerDeleteController(
    private val retry: Retry,
    private val triggerDelete: TriggerDeletePort
) {
    @DeleteMapping("/v1/triggers/{triggerId}")
    fun delete(
        @PathVariable("triggerId") triggerId: TriggerId
    ): ResponseEntity<ApiRequested> = retry {
        triggerDelete(triggerId).accepted()
    }
}