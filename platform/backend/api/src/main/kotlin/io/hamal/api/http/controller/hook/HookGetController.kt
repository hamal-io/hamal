package io.hamal.api.http.controller.hook

import io.hamal.core.adapter.hook.HookGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
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
    private val hookGet: HookGetPort,
    private val namespaceGet: NamespaceGetPort
) {

    @GetMapping("/v1/hooks/{hookId}")
    fun get(@PathVariable("hookId") hookId: HookId): ResponseEntity<ApiHook> = retry {
        hookGet(hookId).let { hook ->
            val namespace = namespaceGet(hook.namespaceId)
            assemble(hook, namespace)
        }
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