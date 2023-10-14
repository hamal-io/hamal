package io.hamal.integration.http

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.HookId
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedInvokeHookReq
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvokeController(
    private val generateDomainId: GenerateDomainId, private val reqCmdRepository: ReqCmdRepository
) {
    @GetMapping("/v1/webhooks/{id}")
    fun webhookGet(
        @PathVariable("id") id: HookId,
        req: HttpServletRequest
    ): ResponseEntity<SubmittedInvokeHookReq> {
        println(req)
        val result = SubmittedInvokeHookReq(
            reqId = generateDomainId(::ReqId), status = ReqStatus.Submitted, id = id
        ).also(reqCmdRepository::queue)
        return ResponseEntity(result, ACCEPTED)
    }

    @PostMapping("/v1/webhooks/{id}")
    fun webhookPost(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @PatchMapping("/v1/webhooks/{id}")
    fun webhookPatch(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @PutMapping("/v1/webhooks/{id}")
    fun webhookPut(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()

    @DeleteMapping("/v1/webhooks/{id}")
    fun webhookDelete(@PathVariable("id") id: HookId): ResponseEntity<Unit> = ResponseEntity.noContent().build()
}
