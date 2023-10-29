package io.hamal.api.http.endpoint.func

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.FuncUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncCodeDeployReq
import io.hamal.lib.sdk.api.ApiSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncDeployCodeController(
    private val retry: Retry,
    private val updateFunc: FuncUpdatePort
) {
    @PatchMapping("/v1/funcs/{funcId}/deploy/{codeVersion}")
    fun deployCode(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("codeVersion") codeVersion: CodeVersion,
    ): ResponseEntity<ApiSubmitted> = retry {
        updateFunc(funcId, ApiFuncCodeDeployReq(codeVersion))
        { it.accepted() }
    }

}