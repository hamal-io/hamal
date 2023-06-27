package io.hamal.backend.instance.web

import io.hamal.backend.instance.service.query.StateQueryService
import io.hamal.lib.sdk.domain.ApiGetStateResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GetStateRoute(
    val queryService: StateQueryService,
) {

    @GetMapping("/v1/funcs/{funcId}/state/{correlationId}")
    fun getState(
        @PathVariable("funcId") stringFuncId: String,
        @PathVariable("correlationId") stringCorId: String,
    ): ResponseEntity<ApiGetStateResponse> {
        TODO()
//        return ResponseEntity.ok(
//            queryService.get(
//                Correlation(
//                    funcId = FuncId(SnowflakeId(stringFuncId.toLong())),
//                    correlationId = CorrelationId(stringCorId)
//                )
//            ).let {
//                ApiGetStateResponse(
//                    correlation = it.correlation,
//                    contentType = it.payload.contentType
//                )
//            }
//        )
    }

}