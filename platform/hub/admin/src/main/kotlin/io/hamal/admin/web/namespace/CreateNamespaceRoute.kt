package io.hamal.admin.web.namespace

import io.hamal.admin.req.SubmitAdminRequest
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateNamespaceReq
import io.hamal.lib.sdk.admin.AdminSubmittedReqWithId
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateNamespaceRoute(
    private val request: SubmitAdminRequest,
) {

    @PostMapping("/v1/namespaces")
    fun createNamespace(
        @RequestBody createNamespace: AdminCreateNamespaceReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val result = request(GroupId.root, createNamespace)
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

    @PostMapping("/v1/groups/{groupId}/namespaces")
    fun createGroupNamespace(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody createNamespace: AdminCreateNamespaceReq
    ): ResponseEntity<AdminSubmittedReqWithId> {
        val result = request(groupId, createNamespace)
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
}