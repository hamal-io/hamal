package io.hamal.api.http.controller.hook

import io.hamal.core.adapter.HookGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiHook
import io.hamal.lib.sdk.api.ApiHook.*
import io.hamal.repository.api.Hook
import io.hamal.repository.api.Flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookGetController(
    private val retry: Retry,
    private val getHook: HookGetPort
) {

    @GetMapping("/v1/hooks/{hookId}")
    fun getHook(@PathVariable("hookId") hookId: HookId): ResponseEntity<ApiHook> = retry {
        getHook(hookId, ::assemble)
    }

    private fun assemble(hook: Hook, flow: Flow) =
        ResponseEntity.ok(
            ApiHook(
                id = hook.id,
                flow = Flow(
                    id = flow.id,
                    name = flow.name
                ),
                name = hook.name
            )
        )
}