package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface ExecGetPort {
    operator fun invoke(execId: ExecId): Exec
}

interface ExecListPort {
    operator fun invoke(query: ExecQuery): List<Exec>
}

interface ExecCompletePort {
    operator fun invoke(
        execId: ExecId,
        req: ExecCompleteRequest,
    ): ExecCompleteRequested
}

interface ExecFailPort {
    operator fun invoke(
        execId: ExecId,
        req: ExecFailRequest,
    ): ExecFailRequested
}

interface ExecPort : ExecGetPort, ExecListPort, ExecCompletePort, ExecFailPort

@Component
class ExecAdapter(
    private val execQueryRepository: ExecQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val requestCmdRepository: RequestCmdRepository
) : ExecPort {

    override fun invoke(execId: ExecId): Exec = execQueryRepository.get(execId)

    override fun invoke(query: ExecQuery): List<Exec> = execQueryRepository.list(query)

    override fun invoke(
        execId: ExecId,
        req: ExecCompleteRequest
    ): ExecCompleteRequested {
        val exec = execQueryRepository.get(execId)
        return ExecCompleteRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(requestCmdRepository::queue)
    }

    override fun invoke(execId: ExecId, req: ExecFailRequest): ExecFailRequested {
        val exec = execQueryRepository.get(execId)
        return ExecFailRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = exec.id,
            result = req.result
        ).also(requestCmdRepository::queue)
    }

}
