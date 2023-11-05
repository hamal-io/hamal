package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FuncDeployPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncDeployController(
    private val retry: Retry,
    private val deploy: FuncDeployPort,
) {
    @PostMapping("/v1/funcs/{funcId}/deploy/{version}")
    fun deploy(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("version") codeVersion: CodeVersion,
    ): ResponseEntity<ApiSubmitted> = retry {
        deploy(funcId, codeVersion) { it.accepted() }
    }

    @PostMapping("/v1/funcs/{funcId}/deploy/latest")
    fun deployLatest(
        @PathVariable("funcId") funcId: FuncId
    ): ResponseEntity<ApiSubmitted> = retry {
        deploy(funcId) { it.accepted() }
    }

}