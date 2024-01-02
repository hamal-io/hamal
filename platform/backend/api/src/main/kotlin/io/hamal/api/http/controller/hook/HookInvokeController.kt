package io.hamal.api.http.controller.hook

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.ReqStatus
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.converter.convertToType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.HookInvokeSubmitted
import jakarta.servlet.http.HttpServletRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvokeController(
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: ReqCmdRepository,
    private val hookQueryRepository: HookQueryRepository,
    private val json: Json
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
        val hook = hookQueryRepository.get(id)

        val result = HookInvokeSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            hookId = id,
            groupId = hook.groupId,
            invocation = HookInvocation(
                method = req.method(),
                headers = req.headers(),
                parameters = req.parameters(),
                content = req.content()
            ),
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

    private fun HttpServletRequest.content(): HookContent {
        require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
        val content = reader.lines().reduce("", String::plus)
        val el = json.decodeFromString<JsonElement>(content)
        require(el is JsonObject)
        return HookContent(el.convertToType())
    }

    private fun HttpServletRequest.method(): HookMethod = when (method.lowercase()) {
        "delete" -> Delete
        "get" -> Get
        "patch" -> Patch
        "post" -> Post
        "put" -> Put
        else -> throw IllegalArgumentException("${method.lowercase()} not supported")
    }

    data class Response(
        val reqId: ReqId,
        val status: ReqStatus,
        val id: HookId
    ) {
        constructor(req: HookInvokeSubmitted) : this(req.id, req.status, req.hookId)
    }
}
