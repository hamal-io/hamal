package io.hamal.api.web.trigger

import io.hamal.core.component.trigger.ListTriggers
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.hub.HubTriggerList
import io.hamal.lib.sdk.hub.HubTriggerList.EventTrigger.Topic
import io.hamal.lib.sdk.hub.HubTriggerList.Trigger.Func
import io.hamal.lib.sdk.hub.HubTriggerList.Trigger.Namespace
import io.hamal.repository.api.EventTrigger
import io.hamal.repository.api.FixedRateTrigger
import io.hamal.repository.api.TriggerQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ListTriggersController(private val listTriggers: ListTriggers) {
    @GetMapping("/v1/groups/{groupId}/triggers")
    fun listGroupTriggers(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubTriggerList> {
        return listTriggers(
            groupId, TriggerQueryRepository.TriggerQuery(
                afterId = triggerId,
                types = TriggerType.values().toSet(),
                limit = limit
            )
        ) { triggers, funcs, namespaces, topics ->
            ResponseEntity.ok(
                HubTriggerList(
                    triggers.map { trigger ->
                        when (trigger) {
                            is FixedRateTrigger -> {
                                HubTriggerList.FixedRateTrigger(
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
                                HubTriggerList.EventTrigger(
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

                            else -> TODO()
                        }
                    }
                )
            )
        }
    }
}
