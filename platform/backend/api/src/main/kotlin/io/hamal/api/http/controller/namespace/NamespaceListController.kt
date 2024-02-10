package io.hamal.api.http.controller.namespace

import io.hamal.core.adapter.NamespaceListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiNamespaceList
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class NamespaceListController(private val listNamespace: NamespaceListPort) {
    @GetMapping("/v1/groups/{groupId}/namespaces")
    fun listNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: NamespaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiNamespaceList> {
        return listNamespace(
            NamespaceQuery(
                afterId = afterId,
                limit = limit,
                groupIds = listOf(groupId)
            )
        ) { namespaces ->
            ResponseEntity.ok(ApiNamespaceList(
                namespaces.map {
                    ApiNamespaceList.Namespace(
                        id = it.id,
                        name = it.name
                    )
                }
            ))
        }
    }
}