package io.hamal.api.web.trigger

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.CreateTriggerPort
import io.hamal.lib.sdk.api.ApiCreateTriggerReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateTriggerController(private val createTrigger: CreateTriggerPort) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody req: ApiCreateTriggerReq
    ): ResponseEntity<ApiSubmittedReq> {
        return createTrigger(req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}
