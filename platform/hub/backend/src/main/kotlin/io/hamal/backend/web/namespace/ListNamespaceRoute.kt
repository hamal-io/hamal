package io.hamal.backend.web.namespace

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.hub.HubNamespaceList
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListNamespaceRoute(
    private val namespaceQueryRepository: NamespaceQueryRepository,
) {
    @GetMapping("/v1/groups/{groupId}/namespaces")
    fun listNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: NamespaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubNamespaceList> {
        val result = namespaceQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(HubNamespaceList(
            result.map {
                HubNamespaceList.Namespace(
                    id = it.id,
                    name = it.name
                )
            }
        ))
    }
}