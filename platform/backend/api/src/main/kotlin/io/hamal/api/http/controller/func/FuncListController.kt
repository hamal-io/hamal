package io.hamal.api.http.controller.func

import io.hamal.core.adapter.NamespaceListPort
import io.hamal.core.adapter.NamespaceTreeGetSubTreePort
import io.hamal.core.adapter.func.FuncListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiFuncList
import io.hamal.lib.sdk.api.ApiFuncList.Func
import io.hamal.lib.sdk.api.ApiFuncList.Func.Namespace
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncListController(
    private val funcList: FuncListPort,
    private val namespaceList: NamespaceListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/funcs")
    fun namespaceFuncList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "10") limit: Limit,
    ): ResponseEntity<ApiFuncList> {
        return list(
            afterId = afterId,
            limit = limit,
            workspaceIds = listOf(),
            namespaceIds = listOf(namespaceId)
        )
    }

    @GetMapping("/v1/funcs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiFuncList> {

        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }

        return funcList(
            FuncQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = workspaceIds,
                namespaceIds = allNamespaceIds
            ),
        ).let { funcs ->
            val namespaces = namespaceList(NamespaceQuery(
                limit = Limit.all,
                namespaceIds = funcs.map { it.namespaceId }
            )).associateBy { it.id }

            ResponseEntity.ok(ApiFuncList(
                funcs.map { func ->
                    val namespace = namespaces[func.namespaceId]!!
                    Func(
                        id = func.id,
                        namespace = Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        name = func.name
                    )
                }
            ))

        }
    }
}