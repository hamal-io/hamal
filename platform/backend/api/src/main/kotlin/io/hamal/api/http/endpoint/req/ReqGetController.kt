package io.hamal.api.http.endpoint.req

import io.hamal.core.adapter.ReqGetPort
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.sdk.api.ApiSubmittedSimpleReq
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
internal class ReqGetController(private val getReq: ReqGetPort) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(@PathVariable("reqId") reqId: ReqId): ResponseEntity<ApiSubmittedSimpleReq> = getReq(reqId) {
        ResponseEntity(ApiSubmittedSimpleReq(it.reqId, it.status), HttpStatus.ACCEPTED)
    }
}