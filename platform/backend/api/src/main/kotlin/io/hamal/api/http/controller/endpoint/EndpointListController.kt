package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.endpoint.EndpointListPort
import io.hamal.core.adapter.func.FuncListPort
import io.hamal.core.adapter.namespace_tree.NamespaceTreeGetSubTreePort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiEndpointList
import io.hamal.lib.sdk.api.ApiEndpointList.Endpoint
import io.hamal.lib.sdk.api.ApiEndpointList.Endpoint.Func
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import io.hamal.repository.api.FuncQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointListController(
    private val endpointList: EndpointListPort,
    private val funcList: FuncListPort,
    private val namespaceTreeGetSubTree: NamespaceTreeGetSubTreePort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/endpoints")
    fun namespaceEndpointList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: EndpointId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiEndpointList> {
        return list(
            afterId = afterId,
            limit = limit,
            workspaceIds = listOf(),
            namespaceIds = listOf(namespaceId)
        )
    }

    @GetMapping("/v1/endpoints")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: EndpointId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiEndpointList> {
        val allNamespaceIds = namespaceIds.flatMap { namespaceId ->
            namespaceTreeGetSubTree(namespaceId).values
        }

        return endpointList(
            EndpointQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = workspaceIds,
                namespaceIds = allNamespaceIds
            ),
            // assembler
        ).let { endpoints ->

            val namespaces = funcList(
                FuncQueryRepository.FuncQuery(
                    limit = Limit.all,
                    funcIds = endpoints.map { it.funcId }
                )).associateBy { it.id }

            ResponseEntity.ok(ApiEndpointList(
                endpoints.map { endpoint ->
                    val funcs = namespaces[endpoint.funcId]!!
                    Endpoint(
                        id = endpoint.id,
                        func = Func(
                            id = funcs.id,
                            name = funcs.name
                        ),
                        name = endpoint.name
                    )
                }
            ))

        }
    }
}