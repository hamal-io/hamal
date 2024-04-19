package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.endpoint.EndpointGetPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.hook.HookGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
import io.hamal.core.adapter.topic.TopicGetPort
import io.hamal.core.adapter.trigger.TriggerGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiTrigger
import io.hamal.repository.api.Trigger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerGetController(
    private val retry: Retry,
    private val triggerGet: TriggerGetPort,
    private val funcGet: FuncGetPort,
    private val namespaceGet: NamespaceGetPort,
    private val topicGet: TopicGetPort,
    private val hookGet: HookGetPort,
    private val endpointGet: EndpointGetPort
) {
    @GetMapping("/v1/triggers/{triggerId}")
    fun get(
        @PathVariable("triggerId") triggerId: TriggerId,
    ): ResponseEntity<ApiTrigger> {
        return retry {
            triggerGet(triggerId).let { trigger ->

                val func = funcGet(trigger.funcId)
                val namespace = namespaceGet(trigger.namespaceId)

                ResponseEntity.ok(
                    when (trigger) {
                        is Trigger.FixedRate -> ApiTrigger.FixedRate(
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

                        is Trigger.Event -> ApiTrigger.Event(
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
                            topic = topicGet(trigger.topicId).let { topic ->
                                ApiTrigger.Event.Topic(
                                    id = topic.id,
                                    name = topic.name
                                )
                            },
                            status = trigger.status
                        )

                        is Trigger.Hook -> ApiTrigger.Hook(
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
                            hook = hookGet(trigger.hookId).let { hook ->
                                ApiTrigger.Hook.Hook(
                                    id = hook.id,
                                    name = hook.name,
                                    method = trigger.hookMethod
                                )
                            },
                            status = trigger.status
                        )

                        is Trigger.Cron -> ApiTrigger.Cron(
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

                        is Trigger.Endpoint -> ApiTrigger.Endpoint(
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
                            endpoint = endpointGet(trigger.endpointId).let { endpoint ->
                                ApiTrigger.Endpoint.Endpoint(
                                    id = endpoint.id,
                                    name = endpoint.name,
                                )
                            },
                            status = trigger.status
                        )
                    }
                )
            }
        }
    }
}