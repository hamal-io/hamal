package io.hamal.api.http.controller.code

import io.hamal.core.adapter.CodeGetPort
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sdk.api.ApiCode
import io.hamal.repository.api.Code
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CodeGetController(private val getCode: CodeGetPort) {
    @GetMapping("/v1/code/{id}")
    fun get(
        @PathVariable("id") codeId: CodeId,
        @RequestParam(required = false, name = "version") codeVersion: CodeVersion?
    ): ResponseEntity<ApiCode> = assemble(getCode(codeId, codeVersion))

    private fun assemble(code: Code) =
        ResponseEntity.ok(
            ApiCode(
                id = code.id,
                value = code.value,
                version = code.version
            )
        )
}