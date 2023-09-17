package io.hamal.admin.web.adhoc

import io.hamal.admin.web.req.Assembler
import io.hamal.core.adapter.adhoc.Adhoc
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminInvokeAdhocReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AdhocController(private val adhoc: io.hamal.core.adapter.adhoc.Adhoc) {
    @PostMapping("/v1/adhoc")
    fun adhoc(@RequestBody req: AdminInvokeAdhocReq) =
        submit(GroupId.root, req)

    @PostMapping("/v1/groups/{groupId}/adhoc")
    fun adhocGroup(@PathVariable("groupId") groupId: GroupId, @RequestBody req: AdminInvokeAdhocReq) =
        submit(groupId, req)

    private fun submit(groupId: GroupId, req: AdminInvokeAdhocReq) =
        adhoc(groupId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
}
