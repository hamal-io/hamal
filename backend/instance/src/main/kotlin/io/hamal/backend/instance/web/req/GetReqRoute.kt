package io.hamal.backend.instance.web.req

import io.hamal.backend.instance.service.query.ReqQueryService
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.SubmittedInvokeAdhocReq
import io.hamal.lib.sdk.domain.ApiAdhocReq
import io.hamal.lib.sdk.domain.ApiReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetReqRoute(
    private val queryService: ReqQueryService
) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(
        @PathVariable("reqId") stringReqId: String,
    ): ResponseEntity<ApiReq> {
//        val result = queryService.get(ReqId(stringReqId))
        val result = queryService.get(ReqId(0)) // FIXME
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