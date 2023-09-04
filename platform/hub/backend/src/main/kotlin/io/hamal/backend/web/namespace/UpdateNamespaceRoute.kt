package io.hamal.backend.web.namespace

import io.hamal.backend.req.SubmitRequest
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.domain.req.UpdateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.hub.ApiSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UpdateNamespaceRoute(
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val request: SubmitRequest,
) {
    @PutMapping("/v1/namespaces/{namespaceId}")
    fun createNamespace(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestBody updateNamespace: UpdateNamespaceReq
    ): ResponseEntity<ApiSubmittedReqWithId> {
        ensureNamespaceExists(namespaceId)
        val result = request(namespaceId, updateNamespace)
        return ResponseEntity(result.let {
            ApiSubmittedReqWithId(
                reqId = it.reqId,
                status = it.status,
                id = it.id
            )
        }, ACCEPTED)
    }

    private fun ensureNamespaceExists(namespaceId: NamespaceId) {
        namespaceQueryRepository.get(namespaceId)
    }
}