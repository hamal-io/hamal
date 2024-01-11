package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.stereotype.Component

interface ExecGetPort {
    operator fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T): T
}

interface ExecListPort {
    operator fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T): T
}

interface ExecCompletePort {
    operator fun <T : Any> invoke(
        execId: ExecId,
        req: ExecCompleteRequest,
        responseHandler: (ExecCompleteRequested) -> T
    ): T
}

interface ExecFailPort {
    operator fun <T : Any> invoke(
        execId: ExecId,
        req: ExecFailRequest,
        responseHandler: (ExecFailRequested) -> T
    ): T
}

interface ExecPort : ExecGetPort, ExecListPort, ExecCompletePort, ExecFailPort

@Component
class ExecAdapter(
    private val execQueryRepository: ExecQueryRepository,
    private val generateDomainId: GenerateId,
    private val reqCmdRepository: RequestCmdRepository
) : ExecPort {

    override fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T) =
        responseHandler(execQueryRepository.get(execId))

    override fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T) =
        responseHandler(execQueryRepository.list(query))

    override fun <T : Any> invoke(
        execId: ExecId,
        req: ExecCompleteRequest,
        responseHandler: (ExecCompleteRequested) -> T
    ): T {
        val exec = execQueryRepository.get(execId)
        return ExecCompleteRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(execId: ExecId, req: ExecFailRequest, responseHandler: (ExecFailRequested) -> T): T {
        val exec = execQueryRepository.get(execId)
        return ExecFailRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = exec.id,
            result = req.result
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

}
