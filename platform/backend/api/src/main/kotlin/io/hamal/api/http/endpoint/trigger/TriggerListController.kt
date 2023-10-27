package io.hamal.api.http.endpoint.trigger

import io.hamal.core.adapter.ListTriggersPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerList.EventTrigger.Topic
import io.hamal.lib.sdk.api.ApiTriggerList.HookTrigger.Hook
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Func
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Namespace
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.HookTrigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class TriggerListController(private val listTriggers: ListTriggersPort) {
    @GetMapping("/v1/triggers")
    fun listGroupTriggers(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId> = listOf(),
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId> = listOf()
    ): ResponseEntity<ApiTriggerList> {
        return listTriggers(
            TriggerQuery(
                afterId = triggerId,
                types = TriggerType.values().toList(),
                limit = limit,
                groupIds = groupIds,
                funcIds = funcIds,
                namespaceIds = namespaceIds
            )
        ) { triggers, funcs, namespaces, topics, hooks ->
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
                                    namespace = Namespace(
                                        id = trigger.namespaceId,
                                        name = namespaces[trigger.namespaceId]!!.name
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
                                    namespace = Namespace(
                                        id = trigger.namespaceId,
                                        name = namespaces[trigger.namespaceId]!!.name
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
                                    namespace = Namespace(
                                        id = trigger.namespaceId,
                                        name = namespaces[trigger.namespaceId]!!.name
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
