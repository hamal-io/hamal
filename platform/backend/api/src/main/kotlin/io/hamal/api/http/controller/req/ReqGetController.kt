package io.hamal.api.http.controller.req

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.ReqGetPort
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.lib.domain.submitted.Submitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
internal class ReqGetController(private val getReq: ReqGetPort) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(@PathVariable("reqId") reqId: ReqId): ResponseEntity<ApiSubmitted> =
        getReq(reqId, Submitted::accepted)
}