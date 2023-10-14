package io.hamal.bridge.web.endpoint.code

import io.hamal.core.adapter.GetCodePort
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
internal class CodeGetBridgeController(private val getCode: GetCodePort) {
    @GetMapping("/b1/code/{codeId}")
    fun getCode(
        @PathVariable("codeId") codeId: CodeId,
        @RequestParam(required = false, name = "code_version", defaultValue = "0") codeVersion: Int
    ) = getCode(codeId, CodeVersion(codeVersion), ::assemble)

    private fun assemble(code: Code) =
        ResponseEntity.ok(
            ApiCode(
                id = code.id,
                value = code.value,
                version = code.version
            )
        )
}