package io.hamal.admin.web.adhoc

import io.hamal.admin.web.req.Assembler
import io.hamal.core.route.adhoc.AdhocRoute
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(
    private val adhocRoute: AdhocRoute
) {

    @PostMapping("/v1/adhoc")
    fun adhoc(
        @RequestBody adhocInvocation: AdminInvokeAdhocReq
    ): ResponseEntity<AdminSubmittedReq> {
        return adhocRoute.adhoc(GroupId.root, adhocInvocation) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
    }

    @PostMapping("/v1/groups/{groupId}/adhoc")
    fun adhocGroup(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody adhocInvocation: AdminInvokeAdhocReq
    ): ResponseEntity<AdminSubmittedReq> {
        return adhocRoute.adhoc(groupId, adhocInvocation) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
    }
}
