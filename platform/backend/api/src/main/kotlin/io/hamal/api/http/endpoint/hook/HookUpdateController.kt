package io.hamal.api.http.endpoint.hook

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.UpdateHookPort
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiUpdateHookReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookUpdateController(private val updateHook: UpdateHookPort) {
    @PatchMapping("/v1/hooks/{hookId}")
    fun createHook(@PathVariable("hookId") hookId: HookId, @RequestBody req: ApiUpdateHookReq) =
        updateHook(hookId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
}