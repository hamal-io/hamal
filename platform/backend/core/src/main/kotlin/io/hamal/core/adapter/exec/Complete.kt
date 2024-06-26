package io.hamal.core.adapter.exec

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
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
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = exec.id,
            statusCode = req.statusCode,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(requestEnqueue::invoke)
    }
}
