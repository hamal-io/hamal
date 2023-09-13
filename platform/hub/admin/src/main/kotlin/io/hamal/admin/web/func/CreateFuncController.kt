package io.hamal.admin.web.func

import io.hamal.admin.web.req.Assembler
import io.hamal.core.component.func.CreateFunc
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminCreateFuncReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateFuncController(private val createFunc: CreateFunc) {

    @PostMapping("/v1/funcs")
    fun createFunc(@RequestBody req: AdminCreateFuncReq) =
        submit(GroupId.root, req)

    @PostMapping("/v1/groups/{groupId}/funcs")
    fun createGroupFunc(@PathVariable("groupId") groupId: GroupId, @RequestBody req: AdminCreateFuncReq) =
        submit(groupId, req)

    private fun submit(groupId: GroupId, req: AdminCreateFuncReq) =
        createFunc(groupId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
}