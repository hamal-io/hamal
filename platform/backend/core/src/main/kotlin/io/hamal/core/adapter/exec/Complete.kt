package io.hamal.core.adapter.exec

import io.hamal.core.adapter.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
import org.springframework.stereotype.Component

fun interface ExecCompletePort {
    operator fun invoke(execId: ExecId, req: ExecCompleteRequest): ExecCompleteRequested
}

@Component
class ExecCompleteAdapter(
    private val execGet: ExecGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : ExecCompletePort {
    override fun invoke(execId: ExecId, req: ExecCompleteRequest): ExecCompleteRequested {
        val exec = execGet(execId)
        return ExecCompleteRequested(
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            execId = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(requestEnqueue::invoke)
    }
}
