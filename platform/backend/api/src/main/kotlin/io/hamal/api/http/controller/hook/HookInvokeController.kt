package io.hamal.api.http.controller.hook

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.HookQueryRepository
import io.hamal.repository.api.RequestCmdRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvokeController(
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: RequestCmdRepository,
    private val hookQueryRepository: HookQueryRepository
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

        val result = HookInvokeRequested(
            id = generateDomainId(::RequestId),
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

    private fun HttpServletRequest.headers(): HookHeaders {
        val builder = HotObject.builder()
        headerNames.asSequence().forEach { headerName ->
            builder[headerName] = getHeader(headerName)
        }
        return HookHeaders(builder.build())
    }

    private fun HttpServletRequest.parameters(): HookParameters {
        val builder = HotObject.builder()
        parameterMap.forEach { (key, value) ->
            builder[key] = value.joinToString(",")
        }
        return HookParameters(builder.build())
    }

    private fun HttpServletRequest.content(): HookContent {
        require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
        val content = reader.lines().reduce("", String::plus)
        TODO()
//        val el = json.decodeFromString<JsonElement>(content)
//        require(el is JsonObject)
//        return HookContent(el.convertToType())
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
        val reqId: RequestId,
        val status: RequestStatus,
        val id: HookId
    ) {
        constructor(req: HookInvokeRequested) : this(req.id, req.status, req.hookId)
    }
}
