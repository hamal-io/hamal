package io.hamal.api.http.endpoint.func

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateFuncPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiCreateFuncReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncCreateController(
    private val retry: Retry,
    private val createFunc: CreateFuncPort
) {
    @PostMapping("/v1/groups/{groupId}/funcs")
    fun createFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestBody req: ApiCreateFuncReq
    ): ResponseEntity<ApiSubmittedReq> = retry {
        createFunc(groupId, req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}