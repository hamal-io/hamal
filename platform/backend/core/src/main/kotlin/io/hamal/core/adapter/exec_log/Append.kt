package io.hamal.core.adapter.exec_log

import io.hamal.core.adapter.exec.ExecGetPort
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.request.ExecLogAppendRequest
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import org.springframework.stereotype.Component

fun interface ExecLogAppendPort {
    operator fun invoke(execId: ExecId, req: ExecLogAppendRequest): ExecLog
}

@Component
class ExecLogAppendAdapter(
    private val execLogCmdRepository: ExecLogCmdRepository,
    private val generateDomainId: GenerateDomainId,
    private val execGet: ExecGetPort
) : ExecLogAppendPort {
    override fun invoke(execId: ExecId, req: ExecLogAppendRequest): ExecLog {
        return execLogCmdRepository.append(
            ExecLogCmdRepository.AppendCmd(
                execLogId = generateDomainId(::ExecLogId),
                execId = execId,
                workspaceId = execGet(execId).workspaceId,
                level = req.level,
                message = req.message,
                timestamp = req.timestamp
            )
        )
    }
}