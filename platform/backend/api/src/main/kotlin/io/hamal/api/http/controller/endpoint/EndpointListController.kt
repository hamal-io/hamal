package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.HookListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiHookList
import io.hamal.lib.sdk.api.ApiHookList.Hook
import io.hamal.lib.sdk.api.ApiHookList.Hook.Flow
import io.hamal.repository.api.HookQueryRepository.HookQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointListController(private val listHook: HookListPort) {

    @GetMapping("/v1/flows/{flowId}/hooks")
    fun flowHookList(
        @PathVariable("flowId") flowId: FlowId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: HookId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiHookList> {
        return listHook(
            HookQuery(
                afterId = afterId,
                limit = limit,
                groupIds = listOf(),
                flowIds = listOf(flowId)
            ),
            // assembler
        ) { hooks, flows ->

            ResponseEntity.ok(ApiHookList(
                hooks.map { hook ->
                    val flow = flows[hook.flowId]!!
                    Hook(
                        id = hook.id,
                        flow = Flow(
                            id = flow.id,
                            name = flow.name
                        ),
                        name = hook.name
                    )
                }
            ))

        }
    }

    @GetMapping("/v1/hooks")
    fun listHook(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: HookId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>,
        @RequestParam(required = false, name = "flow_ids", defaultValue = "") flowIds: List<FlowId>
    ): ResponseEntity<ApiHookList> {
        return listHook(
            HookQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds,
                flowIds = flowIds
            ),
            // assembler
        ) { hooks, flows ->

            ResponseEntity.ok(ApiHookList(
                hooks.map { hook ->
                    val flow = flows[hook.flowId]!!
                    Hook(
                        id = hook.id,
                        flow = Flow(
                            id = flow.id,
                            name = flow.name
                        ),
                        name = hook.name
                    )
                }
            ))

        }
    }
}