package io.hamal.api.http.endpoint.func

import io.hamal.api.http.endpoint.accepted
import io.hamal.core.adapter.FuncUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncUpdateReq
import io.hamal.lib.sdk.api.ApiSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class FuncUpdateController(
    private val retry: Retry,
    private val updateFunc: FuncUpdatePort,
) {
    @PatchMapping("/v1/funcs/{funcId}")
    fun updateFunc(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiFuncUpdateReq
    ): ResponseEntity<ApiSubmitted> = retry {
        updateFunc(funcId, req) { it.accepted() }
    }

    @PostMapping("/v1/funcs/{funcId}/deploy/{version}")
    fun deployCodeVersion(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("version") codeVersion: CodeVersion,
    ): ResponseEntity<ApiSubmitted> = retry {
        updateFunc(funcId, codeVersion) { it.accepted() }
    }
}