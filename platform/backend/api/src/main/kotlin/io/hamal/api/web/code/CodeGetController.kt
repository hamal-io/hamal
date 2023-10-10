package io.hamal.api.web.code

import io.hamal.core.adapter.GetCodePort
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sdk.api.ApiCode
import io.hamal.repository.api.Code
import io.hamal.repository.api.CodeQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CodeGetController(private val getCode: GetCodePort) {
    @GetMapping("/v1/code/{codeId}")
    fun getCode(
        @PathVariable("codeId") codeId: CodeId,
        @RequestParam(required = false, name = "codeVersion", defaultValue = "0") codeVersion: CodeVersion
    ): ResponseEntity<ApiCode> {
        return getCode(codeId, codeVersion, ::assemble)
    }


    private fun assemble(code: Code) = ResponseEntity.ok(
        ApiCode(
            id = code.id,
            value = code.value,
            version = code.version
        )
    )
}