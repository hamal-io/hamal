package io.hamal.core.request.handler.exec

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.request.handler.cmdId
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.request.ExecInvokeRequested
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.InvocationInputs
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.PlanCmd
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.PlannedExec
import io.hamal.repository.api.event.ExecPlannedEvent
import org.springframework.stereotype.Component

@Component
class ExecInvokeHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: InternalEventEmitter,
    private val funcQueryRepository: FuncQueryRepository
) : io.hamal.core.request.RequestHandler<ExecInvokeRequested>(ExecInvokeRequested::class) {

    override fun invoke(req: ExecInvokeRequested) {
        planExec(req).also { emitEvent(req.cmdId(), it) }
    }

    private fun planExec(req: ExecInvokeRequested): PlannedExec {
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
                execId = req.execId,
                flowId = req.flowId,
                groupId = req.groupId,
                correlation = correlation,
                inputs = merge(func?.inputs ?: FuncInputs(), req.inputs),
                code = req.code,
                invocation = req.invocation
            )
        )
    }

    private fun emitEvent(cmdId: CmdId, exec: PlannedExec) {
        eventEmitter.emit(cmdId, ExecPlannedEvent(exec))
    }
}

internal fun InvocationInputs.toExecInputs() = ExecInputs(this.value)
internal fun merge(funcInputs: FuncInputs, invocationInputs: InvocationInputs): ExecInputs {
    val builder = HotObject.builder()
    funcInputs.value.nodes.forEach { builder[it.key] = it.value }
    invocationInputs.value.nodes.forEach { builder[it.key] = it.value }
    return ExecInputs(builder.build())
}