package io.hamal.api.web.func

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.CreateFuncPort
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubCreateFuncReq
import io.hamal.lib.sdk.hub.HubSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateFuncController(private val createFunc: CreateFuncPort) {
    @PostMapping("/v1/groups/{groupId}/funcs")
    fun createFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: HubCreateFuncReq
    ): ResponseEntity<HubSubmittedReq> =
        createFunc(groupId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
}