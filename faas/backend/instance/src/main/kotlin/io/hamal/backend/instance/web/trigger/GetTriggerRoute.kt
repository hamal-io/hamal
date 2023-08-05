package io.hamal.backend.instance.web.trigger

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.domain.Trigger
import io.hamal.lib.domain.vo.TriggerId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTriggerRoute(
    private val triggerQueryRepository: TriggerQueryRepository,
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<Trigger> {
        val result = triggerQueryRepository.get(triggerId)
        return ResponseEntity.ok(result)
    }
}