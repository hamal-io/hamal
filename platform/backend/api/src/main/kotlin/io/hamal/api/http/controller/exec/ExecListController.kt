package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.exec.ExecListPort
import io.hamal.core.adapter.func.FuncListPort
import io.hamal.core.adapter.namespace.NamespaceListPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.trigger.TriggerListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiExecList
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import io.hamal.repository.api.TriggerQueryRepository.TriggerQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecListController(
    private val execList: ExecListPort,
    private val funcList: FuncListPort,
    private val triggerList: TriggerListPort,
    private val namespaceList: NamespaceListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/execs")
    fun namespaceExecList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<ExecId>
    ): ResponseEntity<ApiExecList> {
        return list(
            afterId = afterId,
            limit = limit,
            ids = ids,
            workspaceIds = listOf(),
            funcIds = listOf(),
            namespaceIds = listOf(namespaceId)
        )
    }

    @GetMapping("/v1/execs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "ids", defaultValue = "") ids: List<ExecId>,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiExecList> {
        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }
        return execList(
            ExecQuery(
                afterId = afterId,
                limit = limit,
                execIds = ids,
                workspaceIds = workspaceIds,
                funcIds = funcIds,
                namespaceIds = allNamespaceIds
            )
        ).let { execs ->
            val namespaces = namespaceList(
                NamespaceQuery(limit = Limit.all,
                    namespaceIds = execs.map { it.namespaceId })
            ).associateBy { it.id }

            val funcs = funcList(
                FuncQuery(limit = Limit.all,
                    funcIds = execs.mapNotNull { it.correlation?.funcId })
            ).associateBy { it.id }


            val triggers = triggerList(
                TriggerQuery(
                    limit = Limit.all,
                    funcIds = funcs.keys.toList()
                )
            )


            ResponseEntity.ok(ApiExecList(execs = execs.map {
                val trigger: ApiExecList.Trigger? = when (it.invocation) {
                    is Invocation.Event -> {
                        val event = it.invocation as Invocation.Event
                        ApiExecList.Trigger(event.id, triggers.find { it.id == event.id }!!.status)
                    }

                    is Invocation.Endpoint -> null
                    is Invocation.Hook -> null
                    is Invocation.Schedule -> {
                        val schedule = it.invocation as Invocation.Schedule
                        ApiExecList.Trigger(schedule.id, triggers.find { it.id == schedule.id }!!.status)
                    }

                    else -> null
                }

                ApiExecList.Exec(
                    id = it.id,
                    status = it.status,
                    namespace = namespaces[it.namespaceId]!!.let { namespace ->
                        ApiExecList.Namespace(
                            id = namespace.id, name = namespace.name
                        )
                    },
                    invocation = it.invocation,
                    correlation = it.correlation?.id,
                    func = it.correlation?.funcId?.let { funcId ->
                        funcs[funcId]!!.let { func ->
                            ApiExecList.Func(
                                id = func.id, name = func.name
                            )
                        }
                    },
                    trigger = trigger
                )
            }
            ))
        }
    }
}

