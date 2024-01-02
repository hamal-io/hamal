package io.hamal.api.http.controller.endpoint

import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiExec
import io.hamal.repository.api.*
import io.hamal.lib.domain.submitted.ExecInvokeSubmitted
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

@RestController
internal class EndpointInvokeController(
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: ReqCmdRepository,
    private val execRepository: ExecRepository,
    private val endpointQueryRepository: EndpointQueryRepository,
    private val funcQueryRepository: FuncQueryRepository
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
        return CompletableFuture.supplyAsync {
            val endpoint = endpointQueryRepository.get(id)
            val func = funcQueryRepository.get(endpoint.funcId)
            val invocation = EndpointInvocation(
                method = req.method(),
                headers = req.headers(),
                parameters = req.parameters(),
                content = req.content()
            )
            ResponseEntity.ok(invoke(func, invocation).let { exec ->
                ApiExec(
                    id = exec.id,
                    status = exec.status,
                    correlation = exec.correlation,
                    inputs = exec.inputs,
                    invocation = exec.invocation,
                    result = if (exec is CompletedExec) {
                        exec.result
                    } else if (exec is FailedExec) {
                        exec.result
                    } else {
                        null
                    },
                    state = if (exec is CompletedExec) {
                        exec.state
                    } else {
                        null
                    }
                )
            })
        }
    }

    private fun invoke(func: Func, invocation: EndpointInvocation): Exec {
        val execId = generateDomainId(::ExecId)
        reqCmdRepository.queue(
            ExecInvokeSubmitted(
                id = generateDomainId(::ReqId),
                status = Submitted,
                execId = execId,
                flowId = func.flowId,
                groupId = func.groupId,
                inputs = InvocationInputs(),
                code = ExecCode(
                    id = func.code.id,
                    version = func.deployment.version
                ),
                funcId = func.id,
                correlationId = null,
                invocation = invocation
            )
        )

        val startedAt = TimeUtils.now()
        while (true) {
            with(execRepository.find(execId)) {
                val exec = this
                if (exec != null) {
                    if (exec.status == ExecStatus.Completed) {
                        return exec
                    } else {
                        if (this?.status == ExecStatus.Failed) {
                            return exec as FailedExec
                        } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                            return exec
                        }
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

    private fun HttpServletRequest.headers() = EndpointHeaders(
        MapType(headerNames.asSequence()
            .map { headerName -> headerName to StringType(getHeader(headerName)) }
            .toMap()
            .toMutableMap()
        ))

    private fun HttpServletRequest.parameters() = EndpointParameters(
        MapType(parameterMap.map { (key, value) -> key to StringType(value.joinToString(",")) }.toMap().toMutableMap())
    )

    private fun HttpServletRequest.content(): EndpointContent {
        val content = reader.lines().reduce("", String::plus)
        if (content.isEmpty()) return EndpointContent()

//        require(contentType.startsWith("application/json")) { "Only application/json supported yet" }
//        val el = json.decodeFromString<JsonElement>(content)
//        require(el is JsonObject)
//        return EndpointContent(el.convertToType())
        TODO()
    }

}