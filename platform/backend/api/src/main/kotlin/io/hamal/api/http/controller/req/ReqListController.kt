package io.hamal.api.http.controller.req

import io.hamal.api.http.controller.toApiSubmitted
import io.hamal.core.adapter.ReqListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.sdk.api.ApiReqList
import io.hamal.repository.api.ReqQueryRepository
import io.hamal.lib.domain.submitted.Submitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ReqListController(private val listReqs: ReqListPort) {
    @GetMapping("/v1/reqs")
    fun listReqs(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: ReqId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiReqList> {
        return listReqs(
            ReqQueryRepository.ReqQuery(
                afterId = afterId,
                limit = limit
                // groupId = ...
            ),
        ) { reqs -> ResponseEntity.ok(ApiReqList(reqs.map(Submitted::toApiSubmitted))) }
    }
}