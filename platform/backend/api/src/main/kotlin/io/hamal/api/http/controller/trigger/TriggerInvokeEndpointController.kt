package io.hamal.api.http.controller.trigger

import io.hamal.api.http.controller.json
import io.hamal.core.adapter.trigger.TriggerInvokeEndpointPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.repository.api.Auth
import io.hamal.repository.api.Exec
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
    ): CompletableFuture<ResponseEntity<ExecResult>> {
        return endpointInvoke(
            triggerId = id,
            inputs = InvocationInputs(
                ValueObject.builder().set(
                    "endpoint", ValueObject.builder()
                        .set("method", req.method())
                        .set("headers", req.headers())
                        .set("parameters", req.parameters())
                        .set("body", req.body())
                        .build()
                ).build()
            ),
            auth
        ).thenApply { exec ->
            when (exec) {

                is Exec.Completed -> {
                    ResponseEntity
                        .status(exec.statusCode.intValue)
                        .body(exec.result)

                }

                is Exec.Failed -> {
                    ResponseEntity
                        .status(exec.statusCode.intValue)
                        .body(exec.result)
                }

                else -> {
                    TODO()
                }
            }
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

private fun HttpServletRequest.headers(): ValueObject {
    val builder = ValueObject.builder()
    headerNames.asSequence().forEach { headerName ->
        builder[headerName] = getHeader(headerName)
    }
    return builder.build()
}

private fun HttpServletRequest.parameters(): ValueObject {
    val builder = ValueObject.builder()
    parameterMap.forEach { (key, value) ->
        builder[key] = value.joinToString(",")
    }
    return builder.build()
}

private fun HttpServletRequest.body(): ValueObject {
    val content = reader.lines().reduce("", String::plus)
    if (content.isEmpty()) return ValueObject.empty

    require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
    return json.read(ValueObject::class, content)
}