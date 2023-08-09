package io.hamal.backend.instance.web.trigger

import io.hamal.backend.repository.api.EventTrigger
import io.hamal.backend.repository.api.FixedRateTrigger
import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.backend.repository.api.TriggerQueryRepository
import io.hamal.backend.repository.api.log.LogBrokerRepository
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ApiEventTrigger
import io.hamal.lib.sdk.domain.ApiFixedRateTrigger
import io.hamal.lib.sdk.domain.ApiTrigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetTriggerRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: LogBrokerRepository,
    private val triggerQueryRepository: TriggerQueryRepository,
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<ApiTrigger> {
        val result = triggerQueryRepository.get(triggerId)
        return ResponseEntity.ok(result.let {
            when (val trigger = it) {
                is FixedRateTrigger -> ApiFixedRateTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = ApiTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    inputs = trigger.inputs,
                    duration = trigger.duration
                )

                is EventTrigger -> ApiEventTrigger(
                    id = trigger.id,
                    name = trigger.name,
                    func = ApiTrigger.Func(
                        id = trigger.funcId,
                        name = funcQueryRepository.get(trigger.funcId).name
                    ),
                    inputs = trigger.inputs,
                    topic = ApiEventTrigger.Topic(
                        id = trigger.topicId,
                        name = eventBrokerRepository.getTopic(trigger.topicId).name
                    )
                )
            }
        })
    }
}