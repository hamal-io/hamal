package io.hamal.core.adapter

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain._enum.RequestStatus.Submitted
import io.hamal.lib.domain.request.ExecCompleteRequest
import io.hamal.lib.domain.request.ExecCompleteRequested
import io.hamal.lib.domain.request.ExecFailRequest
import io.hamal.lib.domain.request.ExecFailRequested
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.RequestId
import io.hamal.repository.api.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.NamespaceQueryRepository.NamespaceQuery
import org.springframework.stereotype.Component

interface ExecGetPort {
    operator fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec, Func?) -> T): T
}

interface ExecListPort {
    operator fun <T : Any> invoke(
        query: ExecQuery,
        responseHandler: (List<Exec>, Map<NamespaceId, Namespace>, Map<FuncId, Func>) -> T
    ): T
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
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository,
    private val generateDomainId: GenerateId,
    private val requestCmdRepository: RequestCmdRepository
) : ExecPort {

    override fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec, Func?) -> T): T {
        val exec = execQueryRepository.get(execId)
        val func = exec.correlation?.funcId?.let { funcId ->
            funcQueryRepository.get(funcId)
        }
        return responseHandler(exec, func)
    }

    override fun <T : Any> invoke(
        query: ExecQuery,
        responseHandler: (List<Exec>, Map<NamespaceId, Namespace>, Map<FuncId, Func>) -> T
    ): T {
        val execs = execQueryRepository.list(query)

        val namespaces = namespaceQueryRepository.list(NamespaceQuery(
            limit = Limit.all,
            namespaceIds = execs.map { it.namespaceId }
        )).associateBy { it.id }

        val funcs = funcQueryRepository.list(FuncQuery(
            limit = Limit.all,
            funcIds = execs.mapNotNull { it.correlation?.funcId }
        )).associateBy { it.id }


        return responseHandler(execs, namespaces, funcs)
    }

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
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

    override fun <T : Any> invoke(execId: ExecId, req: ExecFailRequest, responseHandler: (ExecFailRequested) -> T): T {
        val exec = execQueryRepository.get(execId)
        return ExecFailRequested(
            id = generateDomainId(::RequestId),
            status = Submitted,
            execId = exec.id,
            result = req.result
        ).also(requestCmdRepository::queue).let(responseHandler)
    }

}
