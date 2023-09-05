package io.hamal.core.req.handler.exec

import io.hamal.core.event.HubEventEmitter
import io.hamal.core.req.ReqHandler
import io.hamal.core.req.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.PlanCmd
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.PlannedExec
import io.hamal.repository.api.event.ExecPlannedEvent
import io.hamal.repository.api.submitted_req.SubmittedInvokeExecReq
import org.springframework.stereotype.Component

@Component
class InvokeExecHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: HubEventEmitter,
    private val funcQueryRepository: FuncQueryRepository
) : ReqHandler<SubmittedInvokeExecReq>(SubmittedInvokeExecReq::class) {

    override fun invoke(req: SubmittedInvokeExecReq) {
        planExec(req).also { emitEvent(req.cmdId(), it) }
    }

    private fun planExec(req: SubmittedInvokeExecReq): PlannedExec {
        val correlationId = req.correlationId
        val func = req.funcId?.let { funcQueryRepository.get(it) }

        val correlation = if (func != null && correlationId != null) {
            Correlation(correlationId, func.id)
        } else {
            null
        }

        return execCmdRepository.plan(
            PlanCmd(
                id = req.cmdId(),
                execId = req.id,
                groupId = req.groupId,
                correlation = correlation,
                inputs = merge(func?.inputs ?: FuncInputs(), req.inputs),
                code = func?.code ?: req.code ?: throw IllegalStateException("Code not found"),
                events = req.events,
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, exec: PlannedExec) {
        eventEmitter.emit(cmdId, ExecPlannedEvent(exec))
    }
}

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun merge(funcInputs: FuncInputs, invocationInputs: InvocationInputs): ExecInputs {
    val result = ExecInputs()
    funcInputs.value.entries.forEach { result.value[it.key] = it.value }
    invocationInputs.value.entries.forEach { result.value[it.key] = it.value }
    return result
}