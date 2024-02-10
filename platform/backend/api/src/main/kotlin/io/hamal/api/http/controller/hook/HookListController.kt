package io.hamal.api.http.controller.hook

import io.hamal.core.adapter.HookListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiHookList
import io.hamal.lib.sdk.api.ApiHookList.Hook
import io.hamal.lib.sdk.api.ApiHookList.Hook.Namespace
import io.hamal.repository.api.HookQueryRepository.HookQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookListController(private val listHook: HookListPort) {

    @GetMapping("/v1/namespaces/{namespaceId}/hooks")
    fun namespaceHookList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: HookId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiHookList> {
        return listHook(
            HookQuery(
                afterId = afterId,
                limit = limit,
                groupIds = listOf(),
                namespaceIds = listOf(namespaceId)
            ),
            // assembler
        ) { hooks, namespaces ->

            ResponseEntity.ok(ApiHookList(
                hooks.map { hook ->
                    val namespace = namespaces[hook.namespaceId]!!
                    Hook(
                        id = hook.id,
                        namespace = Namespace(
                            id = namespace.id,
                            name = namespace.name
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
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiHookList> {
        return listHook(
            HookQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds,
                namespaceIds = namespaceIds
            ),
            // assembler
        ) { hooks, namespaces ->

            ResponseEntity.ok(ApiHookList(
                hooks.map { hook ->
                    val namespace = namespaces[hook.namespaceId]!!
                    Hook(
                        id = hook.id,
                        namespace = Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        name = hook.name
                    )
                }
            ))

        }
    }
}