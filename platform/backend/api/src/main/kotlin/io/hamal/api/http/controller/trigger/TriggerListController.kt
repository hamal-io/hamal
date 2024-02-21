package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.TriggerListPort
import io.hamal.core.adapter.func.FuncListPort
import io.hamal.core.adapter.hook.HookListPort
import io.hamal.core.adapter.namespace.NamespaceListPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.topic.TopicListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiTriggerList
import io.hamal.lib.sdk.api.ApiTriggerList.Event.Topic
import io.hamal.lib.sdk.api.ApiTriggerList.Hook.Hook
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Func
import io.hamal.lib.sdk.api.ApiTriggerList.Trigger.Namespace
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.HookQueryRepository.HookQuery
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
    private val hookList: HookListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/triggers")
    fun listNamespaceTriggers(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: TriggerId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerType> = listOf(),
    ): ResponseEntity<ApiTriggerList> {
        return list(
            afterId = afterId,
            limit = limit,
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
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId> = listOf(),
        @RequestParam(
            required = false,
            name = "workspace_ids",
            defaultValue = ""
        ) workspaceIds: List<WorkspaceId> = listOf(),
        @RequestParam(
            required = false,
            name = "namespace_ids",
            defaultValue = ""
        ) namespaceIds: List<NamespaceId> = listOf(),
        @RequestParam(required = false, name = "types", defaultValue = "") types: List<TriggerType> = listOf()
    ): ResponseEntity<ApiTriggerList> {
        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }
        return triggerList(
            TriggerQuery(
                afterId = afterId,
                types = types,
                limit = limit,
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

            val hooks = hookList(HookQuery(
                limit = Limit.all,
                hookIds = triggers.filterIsInstance<Trigger.Hook>().map { it.hookId }
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
                                    duration = trigger.duration
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
                                    )
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
                                    hook = Hook(
                                        id = trigger.hookId,
                                        name = hooks[trigger.hookId]!!.name,
                                        method = trigger.hookMethod
                                    )
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
                                    cron = trigger.cron
                                )
                            }
                        }
                    }
                )
            )
        }
    }
}
