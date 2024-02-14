package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.EndpointListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiEndpointList
import io.hamal.lib.sdk.api.ApiEndpointList.Endpoint
import io.hamal.lib.sdk.api.ApiEndpointList.Endpoint.Func
import io.hamal.repository.api.EndpointQueryRepository.EndpointQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointListController(private val listEndpoint: EndpointListPort) {

    @GetMapping("/v1/namespaces/{namespaceId}/endpoints")
    fun namespaceEndpointList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: EndpointId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiEndpointList> {
        return listEndpoint(
            EndpointQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = listOf(),
                namespaceIds = listOf(namespaceId)
            ),
            // assembler
        ) { endpoints, funcs ->

            ResponseEntity.ok(ApiEndpointList(
                endpoints.map { endpoint ->
                    val func = funcs[endpoint.funcId]!!
                    Endpoint(
                        id = endpoint.id,
                        func = Func(
                            id = func.id,
                            name = func.name
                        ),
                        name = endpoint.name
                    )
                }
            ))

        }
    }

    @GetMapping("/v1/endpoints")
    fun listEndpoint(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: EndpointId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiEndpointList> {
        return listEndpoint(
            EndpointQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = workspaceIds,
                namespaceIds = namespaceIds
            ),
            // assembler
        ) { endpoints, namespaces ->

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