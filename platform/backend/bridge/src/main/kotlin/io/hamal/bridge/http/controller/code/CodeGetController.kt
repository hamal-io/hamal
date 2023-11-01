package io.hamal.bridge.http.controller.code

import io.hamal.core.adapter.CodeGetPort
import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.sdk.bridge.BridgeCode
import io.hamal.repository.api.Code
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class CodeGetBridgeController(private val getCode: CodeGetPort) {
    @GetMapping("/b1/code/{id}")
    fun getCode(
        @PathVariable("id") codeId: CodeId,
        @RequestParam(required = false, name = "version", defaultValue = "0") codeVersion: CodeVersion
    ) = getCode(codeId, codeVersion, ::assemble)

    private fun assemble(code: Code) =
        ResponseEntity.ok(
            BridgeCode(
                id = code.id,
                value = code.value,
                version = code.version
            )
        )
}