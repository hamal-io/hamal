package io.hamal.api.http.controller.trigger

import io.hamal.core.adapter.trigger.TriggerInvokeEndpointPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.api.ApiExecEndpoint
import io.hamal.repository.api.Auth
import io.hamal.repository.api.Exec
import io.hamal.repository.record.json
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
internal class TriggerInvokeEndpointController(
    private val endpointInvoke: TriggerInvokeEndpointPort
) {

    @GetMapping("/v1/endpoints/{id}")
    fun invokeGet(@PathVariable("id") id: TriggerId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PostMapping("/v1/endpoints/{id}")
    fun invokePost(@PathVariable("id") id: TriggerId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PatchMapping("/v1/endpoints/{id}")
    fun invokePatch(@PathVariable("id") id: TriggerId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PutMapping("/v1/endpoints/{id}")
    fun invokePut(@PathVariable("id") id: TriggerId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @DeleteMapping("/v1/endpoints/{id}")
    fun invokeDelete(@PathVariable("id") id: TriggerId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    private fun handle(
        id: TriggerId,
        req: HttpServletRequest,
        auth: Auth
    ): CompletableFuture<ResponseEntity<ApiExecEndpoint>> {
        return endpointInvoke(
            triggerId = id,
            inputs = InvocationInputs(
                HotObject.builder()
                    .set("method", req.method())
                    .set("headers", req.headers())
                    .set("parameters", req.parameters())
                    .set("content", req.content())
                    .build()
            ),
            auth
        ).thenApply { exec ->
            ResponseEntity.ok(
                ApiExecEndpoint(
                    id = exec.id,
                    status = exec.status,
                    correlation = exec.correlation?.id,
                    result = if (exec is Exec.Completed) {
                        exec.result
                    } else if (exec is Exec.Failed) {
                        exec.result
                    } else {
                        null
                    }
                )
            )
        }
    }
}

private fun HttpServletRequest.method(): EndpointMethod = when (method.lowercase()) {
    "delete" -> EndpointMethod.Delete
    "get" -> EndpointMethod.Get
    "patch" -> EndpointMethod.Patch
    "post" -> EndpointMethod.Post
    "put" -> EndpointMethod.Put
    else -> throw IllegalArgumentException("${method.lowercase()} not supported")
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
    val content = reader.lines().reduce("", String::plus)
    if (content.isEmpty()) return HotObject.empty

    require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
    return json.deserialize(HotObject::class, content)
}