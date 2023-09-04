package io.hamal.backend.web.func

import io.hamal.backend.req.SubmitRequest
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateFuncRoute(
    private val request: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestBody createFunc: CreateFuncReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        ensureNamespaceIdExists(createFunc)

        val result = request(createFunc)
        return ResponseEntity(
            result.let {
                HubSubmittedReqWithId(
                    reqId = it.reqId,
                    status = it.status,
                    id = it.id
                )
            }, ACCEPTED
        )
    }

    private fun ensureNamespaceIdExists(createFunc: CreateFuncReq) {
        createFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}