package io.hamal.backend.instance.web

import io.hamal.backend.instance.service.cmd.StateCmdService
import io.hamal.backend.instance.service.query.StateQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.StatePayload
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiGetStateResponse
import io.hamal.lib.sdk.domain.ApiSetStateResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class StateController
@Autowired constructor(
    val queryService: StateQueryService,
    val cmdService: StateCmdService
) {

    @GetMapping("/v1/funcs/{funcId}/state/{correlationId}")
    fun getState(
        @PathVariable("funcId") stringFuncId: String,
        @PathVariable("correlationId") stringCorId: String,
    ): ResponseEntity<ApiGetStateResponse> {
        return ResponseEntity.ok(
            queryService.get(
                Correlation(
                    funcId = FuncId(SnowflakeId(stringFuncId.toLong())),
                    correlationId = CorrelationId(stringCorId)
                )
            ).let {
                ApiGetStateResponse(
                    correlation = it.correlation,
                    contentType = it.payload.contentType
                )
            }
        )
    }

    @PostMapping("/v1/funcs/{funcId}/state/{correlationId}")
    fun setState(
        @PathVariable("funcId") stringFuncId: String,
        @PathVariable("correlationId") stringCorId: String,
        @RequestHeader("Content-Type") contentType: String,
        @RequestBody bytes: ByteArray
    ): ResponseEntity<ApiSetStateResponse> {
        val result = cmdService.set(
            cmdId = CmdId(123),
            StateCmdService.StateToSet(
                correlation = Correlation(
                    funcId = FuncId(SnowflakeId(stringFuncId.toLong())),
                    correlationId = CorrelationId(stringCorId)
                ),
                payload = StatePayload(
                    contentType = contentType,
                    bytes = bytes
                )
            )
        )

        return ResponseEntity.ok(
            ApiSetStateResponse(
                correlation = result.correlation,
                contentType = result.payload.contentType
            )
        )
    }

}