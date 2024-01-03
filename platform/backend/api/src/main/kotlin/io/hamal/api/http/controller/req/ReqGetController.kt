package io.hamal.api.http.controller.req

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.RequestGetPort
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
internal class ReqGetController(private val getReq: RequestGetPort) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(@PathVariable("reqId") reqId: RequestId): ResponseEntity<ApiRequested> =
        getReq(reqId, Requested::accepted)
}