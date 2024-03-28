package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.endpoint.EndpointInvokePort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.EndpointId
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.lib.sdk.api.ApiExec
import io.hamal.repository.api.Auth
import io.hamal.repository.api.Exec
import io.hamal.repository.record.json
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
internal class EndpointInvokeController(
    private val endpointInvoke: EndpointInvokePort,
    private val funcGet: FuncGetPort
) {

    @GetMapping("/v1/endpoints/{id}/invoke")
    fun invokeGet(@PathVariable("id") id: EndpointId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PostMapping("/v1/endpoints/{id}/invoke")
    fun invokePost(@PathVariable("id") id: EndpointId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PatchMapping("/v1/endpoints/{id}/invoke")
    fun invokePatch(@PathVariable("id") id: EndpointId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @PutMapping("/v1/endpoints/{id}/invoke")
    fun invokePut(@PathVariable("id") id: EndpointId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    @DeleteMapping("/v1/endpoints/{id}/invoke")
    fun invokeDelete(@PathVariable("id") id: EndpointId, req: HttpServletRequest) =
        handle(id, req, SecurityContext.current)

    private fun handle(
        id: EndpointId,
        req: HttpServletRequest,
        auth: Auth
    ): CompletableFuture<ResponseEntity<ApiExec>> {
        return endpointInvoke(
            endpointId = id,
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
            SecurityContext.with(auth) {
                val func = funcGet(exec.correlation!!.funcId)

                ResponseEntity.ok(
                    ApiExec(
                        id = exec.id,
                        status = exec.status,
                        correlation = exec.correlation?.id,
                        inputs = exec.inputs,
                        result = if (exec is Exec.Completed) {
                            exec.result
                        } else if (exec is Exec.Failed) {
                            exec.result
                        } else {
                            null
                        },
                        state = if (exec is Exec.Completed) {
                            exec.state
                        } else {
                            null
                        },
                        func = ApiExec.Func(
                            id = func.id,
                            name = func.name
                        )
                    )
                )
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