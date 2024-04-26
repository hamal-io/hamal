package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.toApiRequested
import io.hamal.core.adapter.trigger.TriggerInvokePort
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.request.TriggerInvokeRequest
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiRequested
import io.hamal.repository.record.json
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.*

@RestController
internal class TriggerInvokeHookController(
    private val triggerInvoke: TriggerInvokePort
) {
    @GetMapping("/v1/hooks/{id}")
    fun webhookGet(@PathVariable("id") id: TriggerId, req: HttpServletRequest) = handle(id, req)

    @PostMapping("/v1/hooks/{id}")
    fun webhookPost(@PathVariable("id") id: TriggerId, req: HttpServletRequest) = handle(id, req)

    @PatchMapping("/v1/hooks/{id}")
    fun webhookPatch(@PathVariable("id") id: TriggerId, req: HttpServletRequest) = handle(id, req)

    @PutMapping("/v1/hooks/{id}")
    fun webhookPut(@PathVariable("id") id: TriggerId, req: HttpServletRequest) = handle(id, req)

    @DeleteMapping("/v1/hooks/{id}")
    fun webhookDelete(@PathVariable("id") id: TriggerId, req: HttpServletRequest) = handle(id, req)

    private fun handle(id: TriggerId, req: HttpServletRequest): ApiRequested {
        return triggerInvoke(
            id = id,
            req = object : TriggerInvokeRequest {
                override val correlationId = CorrelationId.default
                override val inputs = InvocationInputs(
                    HotObject.builder()
                        .set("headers", req.headers())
                        .set("parameters", req.parameters())
                        .set("content", req.content())
                        .build()
                )
            }).toApiRequested()
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
}
