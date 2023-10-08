package io.hamal.api.web.trigger

import io.hamal.core.adapter.GetTriggerPort
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiEventTrigger
import io.hamal.lib.sdk.api.ApiFixedRateTrigger
import io.hamal.lib.sdk.api.ApiTrigger
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerGetController(private val getTrigger: GetTriggerPort) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<ApiTrigger> {

        return getTrigger(triggerId) { trigger, func, namespace, topic ->
            ResponseEntity.ok(
                when (trigger) {
                    is FixedRateTrigger -> ApiFixedRateTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = ApiTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = ApiTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        duration = trigger.duration
                    )

                    is EventTrigger -> ApiEventTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = ApiTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = ApiTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        topic = ApiEventTrigger.Topic(
                            id = topic!!.id,
                            name = topic.name
                        )
                    )
                }
            )
        }
    }
}