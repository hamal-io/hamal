package io.hamal.api.http.controller.endpoint

import io.hamal.api.http.controller.endpoint.EndpointInvokeController.InvocationResult.*
import io.hamal.core.component.Async
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.api.ApiAdhocInvokeReq
import io.hamal.lib.sdk.api.ApiExec
import io.hamal.repository.api.*
import io.hamal.repository.api.submitted_req.ExecInvokeSubmitted
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture

@RestController
internal class EndpointInvokeController(
    private val generateDomainId: GenerateDomainId,
    private val async: Async,
    private val reqCmdRepository: ReqCmdRepository,
    private val execRepository: ExecRepository
) {

    @PostMapping("/v1/endpoints")
    fun invokeEndpoint(
        @RequestBody req: ApiAdhocInvokeReq
    ): CompletableFuture<ResponseEntity<ApiExec>> {
        return CompletableFuture.supplyAsync {
            println(Thread.currentThread().name)

            ResponseEntity.ok(
                when (val result = invoke(req)) {
                    is Success -> result.exec
                    is Failed -> result.exec
                    is Timeout -> result.exec
                }.let { exec ->
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
                }
            )
        }
    }

    sealed interface InvocationResult {
        data class Success(val exec: CompletedExec) : InvocationResult
        data class Failed(val exec: FailedExec) : InvocationResult
        data class Timeout(val exec: Exec) : InvocationResult
    }

    private fun invoke(req: ApiAdhocInvokeReq): InvocationResult {

        val execId = generateDomainId(::ExecId)

        reqCmdRepository.queue(
            ExecInvokeSubmitted(
                id = generateDomainId(::ReqId),
                status = Submitted,
                execId = execId,
                flowId = FlowId(1),
                groupId = GroupId(1),
                inputs = req.inputs,
                code = ExecCode(value = req.code),
                funcId = null,
                correlationId = null,
                invocation = EndpointInvocation(
                    method = EndpointMethod.Post,
                    headers = EndpointHeaders(),
                    parameters = EndpointParameters(),
                    content = EndpointContent()
                )
            )
        )


        val startedAt = TimeUtils.now()

        while (true) {
            with(execRepository.find(execId)) {
                val exec = this
                if (exec != null) {
                    if (exec.status == ExecStatus.Completed) {
                        return Success(exec as CompletedExec)
                    } else {
                        if (this?.status == ExecStatus.Failed) {
                            return Failed(exec as FailedExec)
                        } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                            return Timeout(exec)
                        }
                    }
                }
            }
        }
    }
}