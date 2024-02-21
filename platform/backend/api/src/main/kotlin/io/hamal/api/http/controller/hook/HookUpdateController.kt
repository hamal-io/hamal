package io.hamal.api.http.controller.hook

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.HookUpdatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiHookUpdateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookUpdateController(
    private val retry: Retry,
    private val hookUpdate: HookUpdatePort
) {
    @PatchMapping("/v1/hooks/{hookId}")
    fun update(
        @PathVariable("hookId") hookId: HookId,
        @RequestBody req: ApiHookUpdateRequest
    ): ResponseEntity<ApiRequested> = retry {
        hookUpdate(hookId, req).accepted()
    }
}