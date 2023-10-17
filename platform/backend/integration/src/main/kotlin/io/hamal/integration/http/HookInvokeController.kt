package io.hamal.integration.http

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain.vo.HookHeaders
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.HookParameters
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.SubmittedInvokeHookReq
import jakarta.servlet.http.HttpServletRequest
import kotlinx.serialization.Serializable
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvokeController(
    private val generateDomainId: GenerateDomainId, private val reqCmdRepository: ReqCmdRepository
) {
    @GetMapping("/v1/webhooks/{id}")
    fun webhookGet(@PathVariable("id") id: HookId, req: HttpServletRequest) = handle(id, req)

    @PostMapping("/v1/webhooks/{id}")
    fun webhookPost(@PathVariable("id") id: HookId, req: HttpServletRequest) = handle(id, req)

    @PatchMapping("/v1/webhooks/{id}")
    fun webhookPatch(@PathVariable("id") id: HookId, req: HttpServletRequest) = handle(id, req)

    @PutMapping("/v1/webhooks/{id}")
    fun webhookPut(@PathVariable("id") id: HookId, req: HttpServletRequest) = handle(id, req)

    @DeleteMapping("/v1/webhooks/{id}")
    fun webhookDelete(@PathVariable("id") id: HookId, req: HttpServletRequest) = handle(id, req)

    private fun handle(id: HookId, req: HttpServletRequest): ResponseEntity<Response> {
        val result = SubmittedInvokeHookReq(
            reqId = generateDomainId(::ReqId),
            status = ReqStatus.Submitted,
            id = id,
            headers = req.headers(),
            parameters = req.parameters()
        ).also(reqCmdRepository::queue)
        return ResponseEntity(Response(result), ACCEPTED)
    }

    private fun HttpServletRequest.headers() = HookHeaders(
        MapType(headerNames.asSequence()
            .map { headerName -> headerName to StringType(getHeader(headerName)) }
            .toMap()
            .toMutableMap()
        ))

    private fun HttpServletRequest.parameters() = HookParameters(
        MapType(parameterMap.map { (key, value) -> key to StringType(value.joinToString(",")) }.toMap().toMutableMap())
    )

    @Serializable
    data class Response(
        val reqId: ReqId,
        val status: ReqStatus,
        val id: HookId
    ) {
        constructor(req: SubmittedInvokeHookReq) : this(req.reqId, req.status, req.id)
    }
}