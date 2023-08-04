package io.hamal.backend.instance.web.trigger

import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ListTriggersResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ListTriggersRoute(
    private val triggerQueryRepository: TriggerQueryRepository
) {
    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "${Long.MAX_VALUE}") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListTriggersResponse> {
        val result = triggerQueryRepository.list {
            this.afterId = triggerId
            this.types = TriggerType.values().toSet()
            this.limit = limit
        }
        return ResponseEntity.ok(
            ListTriggersResponse(
                result.map {
                    ListTriggersResponse.Trigger(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
    }
}
