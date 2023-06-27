package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.domain.req.CreateTriggerReq
import io.hamal.lib.domain.req.SubmittedCreateTriggerReq
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.*

@RestController
open class CreateTriggerRoute(
    private val funcQueryService: FuncQueryService,
    private val request: SubmitRequest
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestBody createTrigger: CreateTriggerReq
    ): ResponseEntity<SubmittedCreateTriggerReq> {
        ensureFuncExists(createTrigger)

        val result = request(createTrigger)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }

    private fun ensureFuncExists(createTrigger: CreateTriggerReq) {
        funcQueryService.get(createTrigger.funcId)
    }
}
