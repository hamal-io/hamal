package io.hamal.admin.web.trigger

import io.hamal.admin.web.req.Assembler

import io.hamal.core.adapter.trigger.CreateTrigger
import io.hamal.lib.sdk.admin.AdminCreateTriggerReq
import io.hamal.lib.sdk.admin.AdminSubmittedReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CreateTriggerController(private val createTrigger: CreateTrigger) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody req: AdminCreateTriggerReq
    ): ResponseEntity<AdminSubmittedReq> {
        return createTrigger(req) {
            ResponseEntity(Assembler.assemble(it), ACCEPTED)
        }
    }
}
