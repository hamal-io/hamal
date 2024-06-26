package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.func.FuncListPort
import io.hamal.core.adapter.namespace.NamespaceListPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.topic.TopicListPort
import io.hamal.core.adapter.trigger.TriggerListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerTypes
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerType.Companion.TriggerType
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerList.Event.Topic
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Func
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Namespace
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.TopicQueryRepository.TopicQuery
import io.hamal.repository.api.Trigger
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class TriggerListController(
    private val triggerList: TriggerListPort,
    private val funcList: FuncListPort,
    private val namespaceList: NamespaceListPort,
    private val topicList: TopicListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/triggers")
    fun listNamespaceTriggers(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerTypes> = listOf(),
    ): ResponseEntity<ApiTriggerList> {
        return list(
            afterId = afterId,
            limit = limit,
            ids = listOf(),
            funcIds = funcIds,
            types = types,
            workspaceIds = listOf(),
            namespaceIds = listOf(namespaceId)
        )
    }

    @GetMapping("/v1/triggers")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<TriggerId>,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId>,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>,
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerTypes>
    ): ResponseEntity<ApiTriggerList> {
        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }
        return triggerList(
            TriggerQuery(
                afterId = afterId,
                types = types.map(::TriggerType),
                limit = limit,
                triggerIds = ids,
                workspaceIds = workspaceIds,
                funcIds = funcIds,
                namespaceIds = allNamespaceIds
            )
        ).let { triggers ->
            val funcs = funcList(FuncQuery(
                limit = Limit.all,
                funcIds = triggers.map { it.funcId }
            )).associateBy { it.id }

            val namespaces = namespaceList(NamespaceQuery(
                limit = Limit.all,
                namespaceIds = triggers.map { it.namespaceId }
            )).associateBy { it.id }

            val topics = topicList(TopicQuery(
                limit = Limit.all,
                topicIds = triggers.filterIsInstance<Trigger.Event>().map { it.topicId }
            )).associateBy { it.id }

            ResponseEntity.ok(
                ApiTriggerList(
                    triggers.map { trigger ->
                        when (trigger) {
                            is Trigger.FixedRate -> {
                                ApiTriggerList.FixedRate(
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
                                    duration = trigger.duration,
                                    status = trigger.status
                                )
                            }

                            is Trigger.Event -> {
                                ApiTriggerList.Event(
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
                                    ),
                                    status = trigger.status
                                )
                            }

                            is Trigger.Hook -> {
                                ApiTriggerList.Hook(
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
                                    status = trigger.status
                                )
                            }

                            is Trigger.Cron -> {
                                ApiTriggerList.Cron(
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
                                    cron = trigger.cron,
                                    status = trigger.status
                                )
                            }

                            is Trigger.Endpoint -> {
                                ApiTriggerList.Endpoint(
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
                                    status = trigger.status
                                )
                            }
                        }
                    }
                )
            )
        }
    }
}
