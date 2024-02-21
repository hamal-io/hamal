package io.hamal.core.adapter.exec

import io.hamal.core.adapter.request.RequestEnqueuePort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
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
            id = generateDomainId(::RequestId),
            status = RequestStatus.Submitted,
            execId = exec.id,
            result = req.result
        ).also(requestEnqueue::invoke)
    }
}