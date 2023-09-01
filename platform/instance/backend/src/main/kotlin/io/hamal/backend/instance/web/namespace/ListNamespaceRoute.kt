package io.hamal.backend.instance.web.namespace

import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.domain.ApiNamespaceList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListNamespaceRoute(
    private val namespaceQueryRepository: NamespaceQueryRepository,
) {
    @GetMapping("/v1/namespaces")
    fun listNamespace(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: NamespaceId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiNamespaceList> {
        val result = namespaceQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(ApiNamespaceList(
            result.map {
                ApiNamespaceList.ApiSimpleNamespace(
                    id = it.id,
                    name = it.name
                )
            }
        ))
    }
}