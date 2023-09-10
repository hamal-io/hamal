package io.hamal.admin.web.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.admin.AdminNamespace
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetNamespaceRoute(
    private val namespaceQueryRepository: NamespaceQueryRepository,
) {
    @GetMapping("/v1/namespaces/{namespaceId}")
    fun getNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
    ): ResponseEntity<AdminNamespace> {
        val result = namespaceQueryRepository.get(namespaceId)
        return ResponseEntity.ok(result.let {
            AdminNamespace(
                id = it.id,
                name = it.name,
                inputs = it.inputs
            )
        })
    }
}