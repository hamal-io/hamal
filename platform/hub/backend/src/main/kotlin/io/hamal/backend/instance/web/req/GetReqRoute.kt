package io.hamal.backend.instance.web.req

import io.hamal.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.sdk.domain.ApiSubmittedReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetReqRoute(
    private val reqQueryRepository: ReqQueryRepository
) {
    @GetMapping("/v1/reqs/{reqId}")
    fun getReq(
        @PathVariable("reqId") reqId: ReqId,
    ): ResponseEntity<ApiSubmittedReq> {
        return ResponseEntity.ok(reqQueryRepository.get(reqId).let(Assembler::assemble))
    }
}