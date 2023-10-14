package io.hamal.api.http.endpoint.hook

import io.hamal.core.adapter.GetHookPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.sdk.api.ApiHook
import io.hamal.lib.sdk.api.ApiHook.*
import io.hamal.repository.api.Hook
import io.hamal.repository.api.Namespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class HookGetController(
    private val retry: Retry,
    private val getHook: GetHookPort
) {

    @GetMapping("/v1/hooks/{hookId}")
    fun getHook(@PathVariable("hookId") hookId: HookId): ResponseEntity<ApiHook> = retry {
        getHook(hookId, ::assemble)
    }

    private fun assemble(hook: Hook, namespace: Namespace) =
        ResponseEntity.ok(
            ApiHook(
                id = hook.id,
                namespace = Namespace(
                    id = namespace.id,
                    name = namespace.name
                ),
                name = hook.name
            )
        )
}