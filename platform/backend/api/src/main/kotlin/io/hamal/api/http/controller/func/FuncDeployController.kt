package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.func.FuncDeployPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncDeployRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncDeployController(
    private val retry: Retry,
    private val funcDeploy: FuncDeployPort,
) {
    @PostMapping("/v1/funcs/{funcId}/deploy")
    fun deploy(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiFuncDeployRequest,
    ): ResponseEntity<ApiRequested> = retry {
        funcDeploy(funcId, req).accepted()
    }
}