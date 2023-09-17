package io.hamal.admin.web.req

import io.hamal.core.adapter.req.ListReq
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.sdk.admin.AdminReqList
import io.hamal.repository.api.ReqQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListReqController(private val listReq: ListReq) {
    @GetMapping("/v1/reqs")
    fun listReqs(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: ReqId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminReqList> {
        return listReq(
            ReqQueryRepository.ReqQuery(
                afterId = afterId,
                limit = limit
                // groupId = ...
            ),
            // assembler
        ) { reqs ->
            ResponseEntity.ok(AdminReqList(
                reqs.map { Assembler.assemble(it) }
            ))
        }
    }
}