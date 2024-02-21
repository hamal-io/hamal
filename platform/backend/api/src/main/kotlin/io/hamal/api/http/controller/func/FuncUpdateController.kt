package io.hamal.api.http.controller.func

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.FuncUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncUpdateController(
    private val retry: Retry,
    private val funcUpdate: FuncUpdatePort,
) {
    @PatchMapping("/v1/funcs/{funcId}")
    fun update(
        @PathVariable("funcId") funcId: FuncId,
        @RequestBody req: ApiFuncUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        funcUpdate(funcId, req).accepted()
    }
}