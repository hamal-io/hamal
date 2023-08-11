package io.hamal.backend.instance.web.req

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.submitted_req.SubmittedReq
import io.hamal.lib.domain.ReqId
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
    ): ResponseEntity<SubmittedReq> {
        return ResponseEntity.ok(reqQueryRepository.get(reqId))
    }
}