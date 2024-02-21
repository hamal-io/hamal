package io.hamal.core.adapter

import io.hamal.lib.domain.GenerateId
import io.hamal.lib.domain.request.ExecLogAppendRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import io.hamal.repository.api.ExecLogCmdRepository.AppendCmd
import io.hamal.repository.api.ExecLogQueryRepository
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.stereotype.Component

interface ExecLogAppendPort {
    operator fun invoke(execId: ExecId, req: ExecLogAppendRequest): ExecLog
}

interface ExecLogListPort {
    operator fun invoke(query: ExecLogQuery): List<ExecLog>
}

interface ExecLogPort : ExecLogAppendPort, ExecLogListPort

@Component
class ExecLogAdapter(
    private val generateDomainId: GenerateId,
    private val execLogCmdRepository: ExecLogCmdRepository,
    private val execLogQueryRepository: ExecLogQueryRepository,
    private val execQueryRepository: ExecQueryRepository
) : ExecLogPort {

    override operator fun invoke(execId: ExecId, req: ExecLogAppendRequest): ExecLog =
        execLogCmdRepository.append(
            AppendCmd(
                execLogId = generateDomainId(::ExecLogId),
                execId = execId,
                workspaceId = execQueryRepository.get(execId).workspaceId,
                level = req.level,
                message = req.message,
                timestamp = req.timestamp
            )
        )

    override fun invoke(query: ExecLogQuery): List<ExecLog> = execLogQueryRepository.list(query)
}
