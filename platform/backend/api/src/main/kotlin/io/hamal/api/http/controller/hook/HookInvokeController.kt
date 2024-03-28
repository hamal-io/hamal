package io.hamal.api.http.controller.hook

import io.hamal.core.adapter.hook.HookInvokePort
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain._enum.HookMethod.*
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.HookInvokeRequested
import io.hamal.lib.domain.vo.HookId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.record.json
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
internal class HookInvokeController(
    private val hookInvoke: HookInvokePort
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
        return ResponseEntity(
            Response(
                hookInvoke(
                    hookId = id,
                    inputs = InvocationInputs(
                        HotObject.builder()
                            .set("method", req.method())
                            .set("headers", req.headers())
                            .set("parameters", req.parameters())
                            .set("content", req.content())
                            .build()
                    ),
                )
            ), ACCEPTED
        )
    }

    private fun HttpServletRequest.headers(): HotObject {
        val builder = HotObject.builder()
        headerNames.asSequence().forEach { headerName ->
            builder[headerName] = getHeader(headerName)
        }
        return builder.build()
    }

    private fun HttpServletRequest.parameters(): HotObject {
        val builder = HotObject.builder()
        parameterMap.forEach { (key, value) ->
            builder[key] = value.joinToString(",")
        }
        return builder.build()
    }

    private fun HttpServletRequest.content(): HotObject {
        require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
        val content = reader.lines().reduce("", String::plus)
        return json.deserialize(HotObject::class, content)
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
        val requestId: RequestId,
        val requestStatus: RequestStatus,
        val id: HookId
    ) {
        constructor(req: HookInvokeRequested) : this(req.requestId, req.requestStatus, req.id)
    }
}
