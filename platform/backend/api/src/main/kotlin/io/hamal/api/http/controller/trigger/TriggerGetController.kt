package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.TriggerGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.*
import io.hamal.repository.api.Trigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerGetController(
    private val retry: Retry,
    private val getTrigger: TriggerGetPort
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun getFunc(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<ApiTrigger> {
        return retry {
            getTrigger(triggerId) { trigger, func, namespace, topic, hook ->
                ResponseEntity.ok(
                    when (trigger) {
                        is Trigger.FixedRate -> ApiFixedRateTrigger(
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
                            duration = trigger.duration,
                            status = trigger.status
                        )

                        is Trigger.Event -> ApiEventTrigger(
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
                            ),
                            status = trigger.status
                        )

                        is Trigger.Hook -> ApiHookTrigger(
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
                            hook = ApiHookTrigger.Hook(
                                id = hook!!.id,
                                name = hook.name,
                                method = trigger.hookMethod
                            ),
                            status = trigger.status
                        )

                        is Trigger.Cron -> ApiCronTrigger(
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
                            cron = trigger.cron,
                            status = trigger.status
                        )
                    }
                )
            }
        }
    }
}