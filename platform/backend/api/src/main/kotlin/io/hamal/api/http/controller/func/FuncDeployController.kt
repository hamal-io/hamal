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


// FIXME-53 implement & test this controller
// - happy cases -- make sure that you create the func and update it a couple of times
//   -- deploy same code version  - ok
//   -- deploy prior code version - ok
//   -- deploy higher code version - ok

// - func does not exist -- exception
// - func does exist but version does not -- exception
@RestController
internal class FuncDeployController(
    private val retry: Retry,
    private val deploy: FuncDeployPort,
) {
    @PostMapping("/v1/funcs/{funcId}/deploy/{version}")
    fun deployCodeVersion(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("version") codeVersion: CodeVersion,
    ): ResponseEntity<ApiSubmitted> = retry {
        deploy(funcId, codeVersion) { it.accepted() }
    }
}