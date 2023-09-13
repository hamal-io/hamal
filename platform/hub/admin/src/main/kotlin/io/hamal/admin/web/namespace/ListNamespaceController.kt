package io.hamal.admin.web.namespace

import io.hamal.core.component.namespace.ListNamespace
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.admin.AdminNamespaceList
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListNamespaceController(private val listNamespace: ListNamespace) {
    @GetMapping("/v1/groups/{groupId}/namespaces")
    fun listNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: NamespaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminNamespaceList> {
        return listNamespace(
            NamespaceQuery(
                afterId = afterId,
                limit = limit
                // group id
            )
        ) { namespaces ->
            ResponseEntity.ok(AdminNamespaceList(
                namespaces.map {
                    AdminNamespaceList.Namespace(
                        id = it.id,
                        name = it.name
                    )
                }
            ))
        }
    }
}