package io.hamal.admin.web.trigger

import io.hamal.core.component.trigger.GetTrigger
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.admin.AdminEventTrigger
import io.hamal.lib.sdk.admin.AdminFixedRateTrigger
import io.hamal.lib.sdk.admin.AdminTrigger
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
    ): ResponseEntity<AdminTrigger> {

        return getTrigger(triggerId) { trigger, func, namespace, topic ->
            ResponseEntity.ok(
                when (trigger) {
                    is FixedRateTrigger -> AdminFixedRateTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = AdminTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = AdminTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        duration = trigger.duration
                    )

                    is EventTrigger -> AdminEventTrigger(
                        id = trigger.id,
                        name = trigger.name,
                        func = AdminTrigger.Func(
                            id = func.id,
                            name = func.name
                        ),
                        namespace = AdminTrigger.Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        inputs = trigger.inputs,
                        topic = AdminEventTrigger.Topic(
                            id = topic!!.id,
                            name = topic.name
                        )
                    )
                }
            )
        }
    }
}