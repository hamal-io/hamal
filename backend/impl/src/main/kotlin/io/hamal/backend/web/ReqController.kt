package io.hamal.backend.web

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.sdk.domain.ApiAdhocRequest
import io.hamal.lib.sdk.domain.ApiRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class ReqController {

    @GetMapping("/v1/requests")
    fun listReqs() {
        TODO()
    }

    @GetMapping("/v1/requests/{reqId}")
    fun getReq(
        @PathVariable("reqId") stringReqId: String,
    ): ResponseEntity<ApiRequest> {
        return ResponseEntity(
            ApiAdhocRequest(
                id = ReqId(123),
                status = ReqStatus.Received,
                execId = ExecId(SnowflakeId(123L)),
                execStatus = ExecStatus.InFlight
            ),
            HttpStatus.OK
        )
    }

}