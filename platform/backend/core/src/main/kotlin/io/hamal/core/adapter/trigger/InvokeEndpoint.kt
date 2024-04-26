package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.exec.ExecFindPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Completed
import io.hamal.lib.domain.vo.ExecStatus.Failed
import io.hamal.repository.api.Auth
import io.hamal.repository.api.Exec
import io.hamal.repository.api.Trigger
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

fun interface TriggerInvokeEndpointPort {
    operator fun invoke(triggerId: TriggerId, inputs: InvocationInputs, auth: Auth): CompletableFuture<Exec>
}

@Component
class TriggerInvokeEndpointAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val triggerGet: TriggerGetPort,
    private val funcGet: FuncGetPort,
    private val execFind: ExecFindPort
) : TriggerInvokeEndpointPort {

    override fun invoke(triggerId: TriggerId, inputs: InvocationInputs, auth: Auth): CompletableFuture<Exec> {
        return CompletableFuture.supplyAsync {
            SecurityContext.with(auth) {
                val trigger = triggerGet(triggerId)
                invoke(trigger, inputs)
            }
        }
    }

    private fun invoke(trigger: Trigger, inputs: InvocationInputs): Exec {
        val execId = generateDomainId(::ExecId)
        val func = funcGet(trigger.funcId)
        requestEnqueue(
            ExecInvokeRequested(
                requestId = generateDomainId(::RequestId),
                requestedBy = SecurityContext.currentAuthId,
                requestStatus = RequestStatus.Submitted,
                id = execId,
                triggerId = trigger.id,
                namespaceId = func.namespaceId,
                workspaceId = func.workspaceId,
                inputs = inputs,
                code = ExecCode(
                    id = func.code.id,
                    version = func.deployment.version
                ),
                funcId = func.id,
                correlationId = CorrelationId.default,
            )
        )

        val startedAt = TimeUtils.now()
        while (true) {
            with(execFind(execId)) {
                val exec = this
                if (exec != null) {
                    if (exec.status == Completed) {
                        return exec
                    } else {
                        if (this?.status == Failed) {
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
