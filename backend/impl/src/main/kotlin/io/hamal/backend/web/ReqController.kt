package io.hamal.backend.web

import io.hamal.backend.service.query.ReqQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecStatus
import io.hamal.lib.sdk.domain.ApiAdhocRequest
import io.hamal.lib.sdk.domain.ApiListReqResponse
import io.hamal.lib.sdk.domain.ApiReq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ReqController(
    @Autowired val queryService: ReqQueryService
) {

    @GetMapping("/v1/reqs")
    fun listReqs(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringComputeId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListReqResponse> {
        val result = queryService.list(
            afterId = ComputeId(stringComputeId),
            limit = limit
        )

        return ResponseEntity.ok(
            ApiListReqResponse(
                result.map {
                    ApiListReqResponse.Req(
                        id = it.id,
                        status = it.status
                    )
                }
            )
        )
    }

    @GetMapping("/v1/reqs/{computeId}")
    fun getReq(
        @PathVariable("computeId") stringComputeId: String,
    ): ResponseEntity<ApiReq> {
        return ResponseEntity(
            ApiAdhocRequest(
                id = ComputeId(123),
                status = ReqStatus.Received,
                execId = ExecId(SnowflakeId(123L)),
                execStatus = ExecStatus.InFlight
            ),
            HttpStatus.OK
        )
    }

}