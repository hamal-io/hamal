package io.hamal.backend.instance.web.namespace

import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.domain.ApiNamespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetNamespaceRoute(
    private val namespaceQueryRepository: NamespaceQueryRepository,
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
    ): ResponseEntity<ApiNamespace> {
        val result = namespaceQueryRepository.get(namespaceId)
        return ResponseEntity.ok(result.let {
            ApiNamespace(
                id = it.id,
                name = it.name,
                inputs = it.inputs
            )
        })
    }
}