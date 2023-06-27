package io.hamal.backend.instance.web

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.State
import io.hamal.lib.domain.req.SetStateReq
import io.hamal.lib.domain.req.SubmittedSetStateReq
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SetStateRoute(
    val request: SubmitRequest
) {
    @PostMapping("/v1/funcs/{funcId}/state/{correlationId}")
    fun setState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
        @RequestBody state: State
    ): ResponseEntity<SubmittedSetStateReq> {
        val result = request(
            SetStateReq(
                funcId = funcId,
                correlationId = correlationId,
                state = state
            )
        )
        return ResponseEntity(result, HttpStatus.ACCEPTED)
        TODO()
//        val result = cmdService.set(
//            cmdId = CmdId(123),
//            StateCmdService.StateToSet(
//                correlation = Correlation(
//                    funcId = FuncId(SnowflakeId(stringFuncId.toLong())),
//                    correlationId = CorrelationId(stringCorId)
//                ),
//                payload = State(
//                    contentType = contentType,
//                    content = bytes
//                )
//            )
//        )
//
//        return ResponseEntity.ok(
//            ApiSetStateResponse(
//                correlation = result.correlation,
//                contentType = result.payload.contentType
//            )
//        )
    }

}