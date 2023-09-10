package io.hamal.admin.web.trigger

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.admin.AdminTriggerList
import io.hamal.lib.sdk.admin.AdminTriggerList.EventTrigger.Topic
import io.hamal.lib.sdk.admin.AdminTriggerList.Trigger.Func
import io.hamal.lib.sdk.admin.AdminTriggerList.Trigger.Namespace
import io.hamal.repository.api.*
import io.hamal.repository.api.log.BrokerRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ListTriggerRoute(
    private val triggerQueryRepository: TriggerQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val funcQueryRepository: FuncQueryRepository,
    private val eventBrokerRepository: BrokerRepository
) {
    @GetMapping("/v1/groups/{groupId}/triggers")
    fun listTrigger(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") triggerId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminTriggerList> {
        val result = triggerQueryRepository.list {
            this.afterId = triggerId
            this.types = TriggerType.values().toSet()
            this.limit = limit
        }

        val namespaces = namespaceQueryRepository.list(result.map { it.namespaceId })
            .associateBy { it.id }

        val funcs = funcQueryRepository.list(result.map { it.funcId })
            .associateBy { it.id }

        val topics = eventBrokerRepository.list(result.filterIsInstance<EventTrigger>().map { it.topicId })
            .associateBy { it.id }

        return ResponseEntity.ok(
            AdminTriggerList(
                result.map { trigger ->
                    when (val t = trigger) {
                        is FixedRateTrigger -> {
                            AdminTriggerList.FixedRateTrigger(
                                id = t.id,
                                name = t.name,
                                func = Func(
                                    id = t.funcId,
                                    name = funcs[t.funcId]!!.name
                                ),
                                namespace = Namespace(
                                    id = t.namespaceId,
                                    name = namespaces[t.namespaceId]!!.name
                                ),
                                duration = t.duration
                            )
                        }

                        is EventTrigger -> {
                            AdminTriggerList.EventTrigger(
                                id = t.id,
                                name = t.name,
                                func = Func(
                                    id = t.funcId,
                                    name = funcs[t.funcId]!!.name
                                ),
                                namespace = Namespace(
                                    id = t.namespaceId,
                                    name = namespaces[t.namespaceId]!!.name
                                ),
                                topic = Topic(
                                    id = t.topicId,
                                    name = topics[t.topicId]!!.name
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
