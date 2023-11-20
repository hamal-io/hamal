package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.TriggerListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerList.EventTrigger.Topic
import io.hamal.lib.sdk.api.ApiTriggerList.HookTrigger.Hook
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Flow
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Func
import io.hamal.repository.api.CronTrigger
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class TriggerListController(private val listTriggers: TriggerListPort) {

    @GetMapping("/v1/flows/{flowId}/triggers")
    fun listFlowTriggers(
        @PathVariable("flowId") flowId: FlowId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerType> = listOf(),
    ): ResponseEntity<ApiTriggerList> {
        return listTriggers(
            TriggerQuery(
                afterId = triggerId,
                types = types,
                limit = limit,
                funcIds = funcIds,
                flowIds = listOf(flowId)
            )
        ) { triggers, funcs, flows, topics, hooks ->
            ResponseEntity.ok(
                ApiTriggerList(
                    triggers.map { trigger ->
                        when (trigger) {
                            is FixedRateTrigger -> {
                                ApiTriggerList.FixedRateTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    duration = trigger.duration
                                )
                            }

                            is EventTrigger -> {
                                ApiTriggerList.EventTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    topic = Topic(
                                        id = trigger.topicId,
                                        name = topics[trigger.topicId]!!.name
                                    )
                                )
                            }

                            is HookTrigger -> {
                                ApiTriggerList.HookTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    hook = Hook(
                                        id = trigger.hookId,
                                        name = hooks[trigger.hookId]!!.name,
                                        methods = trigger.hookMethods
                                    )
                                )
                            }

                            is CronTrigger -> {
                                ApiTriggerList.CronTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    cron = trigger.cron
                                )
                            }

                            else -> TODO()
                        }
                    }
                )
            )
        }
    }

    @GetMapping("/v1/triggers")
    fun listTriggers(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId> = listOf(),
        @RequestParam(required = false, name = "flow_ids", defaultValue = "") flowIds: List<FlowId> = listOf(),
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerType> = listOf()
    ): ResponseEntity<ApiTriggerList> {
        return listTriggers(
            TriggerQuery(
                afterId = triggerId,
                types = types,
                limit = limit,
                groupIds = groupIds,
                funcIds = funcIds,
                flowIds = flowIds
            )
        ) { triggers, funcs, flows, topics, hooks ->
            ResponseEntity.ok(
                ApiTriggerList(
                    triggers.map { trigger ->
                        when (trigger) {
                            is FixedRateTrigger -> {
                                ApiTriggerList.FixedRateTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    duration = trigger.duration
                                )
                            }

                            is EventTrigger -> {
                                ApiTriggerList.EventTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    topic = Topic(
                                        id = trigger.topicId,
                                        name = topics[trigger.topicId]!!.name
                                    )
                                )
                            }

                            is HookTrigger -> {
                                ApiTriggerList.HookTrigger(
                                    id = trigger.id,
                                    name = trigger.name,
                                    func = Func(
                                        id = trigger.funcId,
                                        name = funcs[trigger.funcId]!!.name
                                    ),
                                    flow = Flow(
                                        id = trigger.flowId,
                                        name = flows[trigger.flowId]!!.name
                                    ),
                                    hook = Hook(
                                        id = trigger.hookId,
                                        name = hooks[trigger.hookId]!!.name,
                                        methods = trigger.hookMethods
                                    )
                                )
                            }

                            else -> TODO()
                        }
                    }
                )
            )
        }
    }
}
