package io.hamal.core.adapter.endpoint

import io.hamal.core.adapter.exec.ExecFindPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.repository.api.Exec
import io.hamal.repository.api.Func
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

fun interface EndpointInvokePort {
    operator fun invoke(endpointId: EndpointId, invocation: Invocation.Endpoint): CompletableFuture<Exec>
}

@Component
class EndpointInvokeAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val funcGet: FuncGetPort,
    private val endpointGet: EndpointGetPort,
    private val execFind: ExecFindPort
) : EndpointInvokePort {

    override fun invoke(endpointId: EndpointId, invocation: Invocation.Endpoint): CompletableFuture<Exec> {
        return CompletableFuture.supplyAsync {
            val endpoint = endpointGet(endpointId)
            val func = funcGet(endpoint.funcId)
            invoke(func, invocation)
        }
    }

    private fun invoke(func: Func, invocation: Invocation.Endpoint): Exec {
        val execId = generateDomainId(::ExecId)
        requestEnqueue(
            ExecInvokeRequested(
                id = generateDomainId(::RequestId),
                status = RequestStatus.Submitted,
                execId = execId,
                namespaceId = func.namespaceId,
                workspaceId = func.workspaceId,
                inputs = InvocationInputs(),
                code = ExecCode(
                    id = func.code.id,
                    version = func.deployment.version
                ),
                funcId = func.id,
                correlationId = CorrelationId.default,
                invocation = invocation
            )
        )

        val startedAt = TimeUtils.now()
        while (true) {
            with(execFind(execId)) {
                val exec = this
                if (exec != null) {
                    if (exec.status == io.hamal.lib.domain.vo.ExecStatus.Completed) {
                        return exec
                    } else {
                        if (this?.status == io.hamal.lib.domain.vo.ExecStatus.Failed) {
                            return exec as Exec.Failed
                        } else if (startedAt.plusSeconds(5).isBefore(TimeUtils.now())) {
                            return exec
                        }
                    }
                }
            }
        }
    }
}
