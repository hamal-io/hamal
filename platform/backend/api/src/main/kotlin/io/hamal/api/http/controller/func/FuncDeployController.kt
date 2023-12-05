package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FuncDeployPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncDeployReq
import io.hamal.lib.sdk.api.ApiSubmitted
import io.hamal.repository.api.submitted_req.FuncDeploySubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class FuncDeployController(
    private val retry: Retry,
    private val deploy: FuncDeployPort,
) {

    @PostMapping("/v1/funcs/deploy/{funcId}")
    fun deploy(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiFuncDeployReq,
    ): ResponseEntity<ApiSubmitted> = retry {
        deploy(funcId, req, FuncDeploySubmitted::accepted)
    }
}