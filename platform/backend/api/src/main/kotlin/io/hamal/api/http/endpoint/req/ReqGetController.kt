package io.hamal.api.http.endpoint.req

import io.hamal.core.adapter.GetReqPort
import io.hamal.lib.domain.ReqId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ReqGetController(private val getReq: GetReqPort) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(
        @PathVariable("reqId") reqId: ReqId,
    ) = getReq(reqId) {
        ResponseEntity(Assembler.assemble(it), HttpStatus.ACCEPTED)
    }
}