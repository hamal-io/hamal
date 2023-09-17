package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.repository.api.*
import io.hamal.repository.api.ExecLogCmdRepository.LogCmd
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.request.AppendExecLogCmd
import org.springframework.stereotype.Component

interface AppendExecLogPort {
    operator fun <T : Any> invoke(execId: ExecId, req: AppendExecLogCmd, responseHandler: (ExecLog) -> T): T
}

interface GetExecPort {
    operator fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T): T
}

interface ListExecsPort {
    operator fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T): T
}

interface ListExecLogsPort {
    operator fun <T : Any> invoke(execId: ExecId, query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T): T
}

interface ListExecsLogsPort {
    operator fun <T : Any> invoke(query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T): T
}

interface ExecPort : AppendExecLogPort, GetExecPort, ListExecsPort, ListExecLogsPort, ListExecsLogsPort


@Component
class ExecAdapter(
    private val generateDomainId: GenerateDomainId,
    private val execLogCmdRepository: ExecLogCmdRepository,
    private val execLogQueryRepository: ExecLogQueryRepository,
    private val execQueryRepository: ExecQueryRepository
) : ExecPort {

    override operator fun <T : Any> invoke(execId: ExecId, req: AppendExecLogCmd, responseHandler: (ExecLog) -> T): T =
        responseHandler(
            execLogCmdRepository.append(
                LogCmd(
                    id = generateDomainId(::ExecLogId),
                    execId = execId,
                    level = req.level,
                    message = req.message,
                    localAt = req.localAt
                )
            )
        )

    override fun <T : Any> invoke(execId: ExecId, responseHandler: (Exec) -> T) =
        responseHandler(execQueryRepository.get(execId))

    override fun <T : Any> invoke(query: ExecQuery, responseHandler: (List<Exec>) -> T) =
        responseHandler(execQueryRepository.list(query))

    override fun <T : Any> invoke(execId: ExecId, query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T) =
        responseHandler(execLogQueryRepository.list(execId, query))

    override fun <T : Any> invoke(query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T) =
        responseHandler(execLogQueryRepository.list(query))
}
