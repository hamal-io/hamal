package io.hamal.api.http.controller.request

import io.hamal.api.http.controller.toApiRequested
import io.hamal.core.adapter.request.RequestListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sdk.api.ApiRequestList
import io.hamal.repository.api.RequestQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class RequestListController(private val requestList: RequestListPort) {
    @GetMapping("/v1/requests")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: RequestId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiRequestList> {
        return requestList(
            RequestQueryRepository.RequestQuery(
                afterId = afterId,
                limit = limit
                // workspaceId = ...
            ),
        ).let { reqs -> ResponseEntity.ok(ApiRequestList(reqs.map(Requested::toApiRequested))) }
    }
}