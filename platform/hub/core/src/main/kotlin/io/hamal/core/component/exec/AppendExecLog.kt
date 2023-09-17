package io.hamal.core.component.exec

import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.repository.api.ExecLog
import io.hamal.repository.api.ExecLogCmdRepository
import io.hamal.repository.api.ExecLogCmdRepository.LogCmd
import io.hamal.request.AppendExecLogCmd
import org.springframework.stereotype.Component

@Component
class AppendExecLog(
    private val generateDomainId: GenerateDomainId,
    private val execLogCmdRepository: ExecLogCmdRepository
) {
    operator fun <T : Any> invoke(
        execId: ExecId,
        req: AppendExecLogCmd,
        responseHandler: (ExecLog) -> T
    ): T = responseHandler(
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
}

