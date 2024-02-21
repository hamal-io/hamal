package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.EndpointGetPort
import io.hamal.core.adapter.FuncGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.sdk.api.ApiEndpoint
import io.hamal.lib.sdk.api.ApiEndpoint.*
import io.hamal.repository.api.Endpoint
import io.hamal.repository.api.Func
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EndpointGetController(
    private val retry: Retry,
    private val getEndpoint: EndpointGetPort,
    private val funcGet: FuncGetPort
) {

    @GetMapping("/v1/endpoints/{endpointId}")
    fun get(@PathVariable("endpointId") endpointId: EndpointId): ResponseEntity<ApiEndpoint> = retry {
        getEndpoint(endpointId).let { endpoint ->
            assemble(endpoint, funcGet(endpoint.funcId))
        }
    }

    private fun assemble(endpoint: Endpoint, func: Func) =
        ResponseEntity.ok(
            ApiEndpoint(
                id = endpoint.id,
                func = Func(
                    id = func.id,
                    name = func.name
                ),
                name = endpoint.name
            )
        )
}