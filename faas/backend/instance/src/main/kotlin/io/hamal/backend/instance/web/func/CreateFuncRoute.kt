package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.req.SubmittedCreateFuncReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateFuncRoute(
    private val request: SubmitRequest,
) {
    @PostMapping("/v1/funcs")
    fun createFunc(
        @RequestBody createFunc: CreateFuncReq
    ): ResponseEntity<SubmittedCreateFuncReq> {
        val result = request(createFunc)
        return ResponseEntity(result, ACCEPTED)
    }
}