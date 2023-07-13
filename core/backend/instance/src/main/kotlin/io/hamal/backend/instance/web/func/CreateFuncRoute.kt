package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import io.hamal.lib.domain.vo.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CreateFuncRoute(
    private val request: SubmitRequest,
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestBody createFunc: CreateFuncReq
    ): ResponseEntity<SubmittedCreateFuncReq> {
        val result = request(createFunc)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }
}