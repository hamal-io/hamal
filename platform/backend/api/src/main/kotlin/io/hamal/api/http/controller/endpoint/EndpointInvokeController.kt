package io.hamal.api.http.controller.endpoint

import io.hamal.core.adapter.endpoint.EndpointInvokePort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiExec
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
    fun invokeGet(@PathVariable("id") id: EndpointId, req: HttpServletRequest) = handle(id, req)

    @PostMapping("/v1/endpoints/{id}/invoke")
    fun invokePost(@PathVariable("id") id: EndpointId, req: HttpServletRequest) = handle(id, req)

    @PatchMapping("/v1/endpoints/{id}/invoke")
    fun invokePatch(@PathVariable("id") id: EndpointId, req: HttpServletRequest) = handle(id, req)

    @PutMapping("/v1/endpoints/{id}/invoke")
    fun invokePut(@PathVariable("id") id: EndpointId, req: HttpServletRequest) = handle(id, req)

    @DeleteMapping("/v1/endpoints/{id}/invoke")
    fun invokeDelete(@PathVariable("id") id: EndpointId, req: HttpServletRequest) = handle(id, req)

    private fun handle(id: EndpointId, req: HttpServletRequest): CompletableFuture<ResponseEntity<ApiExec>> {
        return endpointInvoke(
            endpointId = id,
            invocation = Invocation.Endpoint(
                method = req.method(),
                headers = req.headers(),
                parameters = req.parameters(),
                content = req.content()
            )
        ).thenApply { exec ->
            val func = funcGet(exec.correlation!!.funcId)

            ResponseEntity.ok(
                ApiExec(
                    id = exec.id,
                    status = exec.status,
                    correlation = exec.correlation?.id,
                    inputs = exec.inputs,
                    invocation = exec.invocation,
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

private fun HttpServletRequest.method(): EndpointMethod = when (method.lowercase()) {
    "delete" -> EndpointMethod.Delete
    "get" -> EndpointMethod.Get
    "patch" -> EndpointMethod.Patch
    "post" -> EndpointMethod.Post
    "put" -> EndpointMethod.Put
    else -> throw IllegalArgumentException("${method.lowercase()} not supported")
}

private fun HttpServletRequest.headers(): EndpointHeaders {
    val builder = HotObject.builder()
    headerNames.asSequence().forEach { headerName ->
        builder[headerName] = getHeader(headerName)
    }
    return EndpointHeaders(builder.build())
}

private fun HttpServletRequest.parameters(): EndpointParameters {
    val builder = HotObject.builder()
    parameterMap.forEach { (key, value) ->
        builder[key] = value.joinToString(",")
    }
    return EndpointParameters(builder.build())
}

private fun HttpServletRequest.content(): EndpointContent {
    val content = reader.lines().reduce("", String::plus)
    if (content.isEmpty()) return EndpointContent()

    require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
    return EndpointContent(json.deserialize(HotObject::class, content))
}