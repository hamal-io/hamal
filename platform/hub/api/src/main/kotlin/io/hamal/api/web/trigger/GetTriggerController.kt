package io.hamal.api.web.trigger

import io.hamal.core.component.trigger.GetTrigger
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.hub.HubEventTrigger
import io.hamal.lib.sdk.hub.HubFixedRateTrigger
import io.hamal.lib.sdk.hub.HubTrigger
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetTriggerController(private val getTrigger: GetTrigger) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<HubTrigger> {

        return getTrigger(triggerId) { trigger, func, namespace, topic ->
            ResponseEntity.ok(
                when (trigger) {
                    is FixedRateTrigger -> HubFixedRateTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = HubTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = HubTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        duration = trigger.duration
                    )

                    is EventTrigger -> HubEventTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = HubTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = HubTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        topic = HubEventTrigger.Topic(
                            id = topic!!.id,
                            name = topic.name
                        )
                    )
                }
            )
        }
    }
}