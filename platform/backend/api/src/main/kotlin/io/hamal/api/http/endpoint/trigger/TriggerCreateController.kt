package io.hamal.api.http.endpoint.trigger

import io.hamal.api.http.endpoint.req.Assembler
import io.hamal.core.adapter.CreateTriggerPort
import io.hamal.lib.sdk.api.ApiCreateTriggerReq
import io.hamal.lib.sdk.api.ApiSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class TriggerCreateController(private val createTrigger: CreateTriggerPort) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody req: ApiCreateTriggerReq
    ): ResponseEntity<ApiSubmittedReq> {
        return createTrigger(req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}
