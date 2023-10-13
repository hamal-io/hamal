package io.hamal.integration.http

import io.hamal.lib.domain.vo.HookId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvocationController {
    @GetMapping("/v1/webhooks/{id}")
    fun webhookGet(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @PostMapping("/v1/webhooks/{id}")
    fun webhookPost(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @PatchMapping("/v1/webhooks/{id}")
    fun webhookPatch(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @PutMapping("/v1/webhooks/{id}")
    fun webhookPut(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @DeleteMapping("/v1/webhooks/{id}")
    fun webhookDelete(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()
}
