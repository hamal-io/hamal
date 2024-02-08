package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.FlowListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiFlowList
import io.hamal.repository.api.FlowQueryRepository.FlowQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceListController(private val listFlow: FlowListPort) {
    @GetMapping("/v1/groups/{groupId}/flows")
    fun listFlow(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FlowId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiFlowList> {
        return listFlow(
            FlowQuery(
                afterId = afterId,
                limit = limit,
                groupIds = listOf(groupId)
            )
        ) { flows ->
            ResponseEntity.ok(ApiFlowList(
                flows.map {
                    ApiFlowList.Flow(
                        id = it.id,
                        name = it.name,
                        type = it.type
                    )
                }
            ))
        }
    }
}