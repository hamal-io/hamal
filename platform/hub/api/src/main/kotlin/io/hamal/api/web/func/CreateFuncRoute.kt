package io.hamal.api.web.func

import io.hamal.core.req.SubmitRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubCreateFuncReq
import io.hamal.lib.sdk.hub.HubSubmittedReqWithId
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateFuncRoute(
    private val request: SubmitRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @PostMapping("/v1/groups/{groupId}/funcs")
    fun createFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createFunc: HubCreateFuncReq
    ): ResponseEntity<HubSubmittedReqWithId> {
        ensureNamespaceIdExists(createFunc)

        val result = request(groupId, createFunc)
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

    private fun ensureNamespaceIdExists(createFunc: HubCreateFuncReq) {
        createFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}