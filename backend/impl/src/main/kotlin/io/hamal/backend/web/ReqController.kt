package io.hamal.backend.web

import io.hamal.backend.service.query.ReqQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.sdk.domain.ApiAdhocReq
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
        @RequestParam(required = false, name = "stringReqId", defaultValue = "0") stringReqId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListReqResponse> {
        val result = queryService.list(
            afterId = ReqId(stringReqId),
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

    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(
        @PathVariable("reqId") stringReqId: String,
    ): ResponseEntity<ApiReq> {
        val result = queryService.get(ReqId(stringReqId))
        return when (result) {
            is SubmittedInvokeAdhocReq -> ResponseEntity(
                ApiAdhocReq(
                    id = result.id,
                    status = result.status,
                    execId = result.execId
                ),
                HttpStatus.OK
            )

            else -> TODO()
        }
    }

}