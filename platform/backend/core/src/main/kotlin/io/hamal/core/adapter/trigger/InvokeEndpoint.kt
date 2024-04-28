package io.hamal.core.adapter.trigger

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.Completed
import io.hamal.lib.domain.vo.ExecStatus.Failed
import io.hamal.repository.api.*
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

fun interface TriggerInvokeEndpointPort {
    operator fun invoke(triggerId: TriggerId, inputs: InvocationInputs, auth: Auth): CompletableFuture<Exec>
}

@Component
class TriggerInvokeEndpointAdapter(
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort,
    private val triggerRepository: TriggerRepository,
    private val funcRepository: FuncRepository,
    private val execRepository: ExecRepository
) : TriggerInvokeEndpointPort {

    override fun invoke(triggerId: TriggerId, inputs: InvocationInputs, auth: Auth): CompletableFuture<Exec> {
        return CompletableFuture.supplyAsync {
            val trigger = triggerRepository.get(triggerId)
            invoke(trigger, inputs)
        }
    }

    private fun invoke(trigger: Trigger, inputs: InvocationInputs): Exec {
        val execId = generateDomainId(::ExecId)
        val func = funcRepository.get(trigger.funcId)
        requestEnqueue(
            ExecInvokeRequested(
                requestId = generateDomainId(::RequestId),
                requestedBy = AuthId.anonymous, // FIXME
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
            with(execRepository.find(execId)) {
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
