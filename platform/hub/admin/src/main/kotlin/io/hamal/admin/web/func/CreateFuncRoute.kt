package io.hamal.admin.web.func

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateFuncReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateFuncRoute(
    private val request: SubmitAdminRequest,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {

    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestBody createFunc: AdminCreateFuncReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        ensureNamespaceIdExists(createFunc)

        val result = request(GroupId.root, createFunc)
        return ResponseEntity(
            result.let {
                AdminSubmittedReqWithId(
                    reqId = it.reqId,
                    status = it.status,
                    id = it.id
                )
            }, ACCEPTED
        )
    }

    @PostMapping("/v1/groups/{groupId}/funcs")
    fun createGroupFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createFunc: AdminCreateFuncReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        ensureNamespaceIdExists(createFunc)

        val result = request(groupId, createFunc)
        return ResponseEntity(
            result.let {
                AdminSubmittedReqWithId(
                    reqId = it.reqId,
                    status = it.status,
                    id = it.id
                )
            }, ACCEPTED
        )
    }

    private fun ensureNamespaceIdExists(createFunc: AdminCreateFuncReq) {
        createFunc.namespaceId?.let { namespaceQueryRepository.get(it) }
    }
}