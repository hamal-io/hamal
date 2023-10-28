package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import io.hamal.repository.api.ExecLogCmdRepository.AppendCmd
import io.hamal.repository.api.ExecLogQueryRepository
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecQueryRepository
import io.hamal.request.AppendExecLogReq
import org.springframework.stereotype.Component

interface ExecLogAppendPort {
    operator fun <T : Any> invoke(execId: ExecId, req: AppendExecLogReq, responseHandler: (ExecLog) -> T): T
}

interface ExecLogListPort {
    operator fun <T : Any> invoke(query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T): T
}

interface ExecLogPort : ExecLogAppendPort, ExecLogListPort

@Component
class ExecLogAdapter(
    private val generateDomainId: GenerateDomainId,
    private val execLogCmdRepository: ExecLogCmdRepository,
    private val execLogQueryRepository: ExecLogQueryRepository,
    private val execQueryRepository: ExecQueryRepository
) : ExecLogPort {

    override operator fun <T : Any> invoke(execId: ExecId, req: AppendExecLogReq, responseHandler: (ExecLog) -> T): T =
        responseHandler(
            execLogCmdRepository.append(
                AppendCmd(
                    execLogId = generateDomainId(::ExecLogId),
                    execId = execId,
                    groupId = execQueryRepository.get(execId).groupId,
                    level = req.level,
                    message = req.message,
                    localAt = req.localAt
                )
            )
        )

    override fun <T : Any> invoke(query: ExecLogQuery, responseHandler: (List<ExecLog>) -> T) =
        responseHandler(execLogQueryRepository.list(query))
}
