package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.ReqStatus.Submitted
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ReqId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ReqCmdRepository
import io.hamal.repository.api.submitted_req.ExecCompleteSubmitted
import io.hamal.repository.api.submitted_req.ExecFailSubmitted
import io.hamal.request.CompleteExecReq
import io.hamal.request.FailExecReq
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
        req: CompleteExecReq,
        responseHandler: (ExecCompleteSubmitted) -> T
    ): T
}

interface ExecFailPort {
    operator fun <T : Any> invoke(
        execId: ExecId,
        req: FailExecReq,
        responseHandler: (ExecFailSubmitted) -> T
    ): T
}

interface ExecPort : ExecGetPort, ExecListPort, ExecCompletePort, ExecFailPort

@Component
class ExecAdapter(
    private val execQueryRepository: ExecQueryRepository,
    private val generateDomainId: GenerateDomainId,
    private val reqCmdRepository: ReqCmdRepository
) : ExecPort {

    override fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T) =
        responseHandler(execQueryRepository.get(execId))

    override fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T) =
        responseHandler(execQueryRepository.list(query))

    override fun <T : Any> invoke(
        execId: ExecId,
        req: CompleteExecReq,
        responseHandler: (ExecCompleteSubmitted) -> T
    ): T {
        val exec = execQueryRepository.get(execId)
        return ExecCompleteSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = exec.id,
            result = req.result,
            state = req.state,
            events = req.events
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(execId: ExecId, req: FailExecReq, responseHandler: (ExecFailSubmitted) -> T): T {
        val exec = execQueryRepository.get(execId)
        return ExecFailSubmitted(
            id = generateDomainId(::ReqId),
            status = Submitted,
            execId = exec.id,
            result = req.result
        ).also(reqCmdRepository::queue).let(responseHandler)
    }

}
