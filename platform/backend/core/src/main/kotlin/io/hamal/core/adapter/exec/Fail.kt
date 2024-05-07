package io.hamal.core.adapter.exec

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatuses.Submitted
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestStatus.Companion.RequestStatus
import org.springframework.stereotype.Component

fun interface ExecFailPort {
    operator fun invoke(execId: ExecId, req: ExecFailRequest): ExecFailRequested
}

@Component
class ExecFailAdapter(
    private val execGet: ExecGetPort,
    private val generateDomainId: GenerateDomainId,
    private val requestEnqueue: RequestEnqueuePort
) : ExecFailPort {
    override fun invoke(execId: ExecId, req: ExecFailRequest): ExecFailRequested {
        val exec = execGet(execId)
        return ExecFailRequested(
            requestId = generateDomainId(::RequestId),
            requestedBy = SecurityContext.currentAuthId,
            requestStatus = RequestStatus(Submitted),
            id = exec.id,
            statusCode = req.statusCode,
            result = req.result
        ).also(requestEnqueue::invoke)
    }
}