package io.hamal.backend.instance.web.req

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.sdk.domain.ListSubmittedReqsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListReqsRoute(
    private val reqQueryRepository: ReqQueryRepository
) {
    @GetMapping("/v1/reqs")
    fun listReqs(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: ReqId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListSubmittedReqsResponse> {
        val result = reqQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(ListSubmittedReqsResponse(result))
    }
}