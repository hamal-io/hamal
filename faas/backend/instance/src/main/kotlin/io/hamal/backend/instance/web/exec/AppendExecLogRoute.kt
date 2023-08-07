package io.hamal.backend.instance.web.exec

import io.hamal.backend.repository.api.ExecLogCmdRepository
import io.hamal.backend.repository.api.ExecLogCmdRepository.LogCmd
import io.hamal.lib.domain.ExecLogLevel
import io.hamal.lib.domain.LocalAt
import io.hamal.lib.domain.LogMessage
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogId
import io.hamal.lib.domain.vo.port.GenerateDomainId
import kotlinx.serialization.Serializable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AppendExecLogRoute(
    private val generateDomainId: GenerateDomainId,
    private val execLogCmdRepository: ExecLogCmdRepository
) {

    @PostMapping("/v1/exec/{execId}/logs")
    fun appendExecLog(
        @PathVariable("execId") execId: ExecId,
        @RequestBody appendExecLog: AppendExecLogCmd
    ): ResponseEntity<Unit> {
        execLogCmdRepository.append(
            LogCmd(
                id = generateDomainId(::ExecLogId),
                execId = execId,
                level = appendExecLog.level,
                message = appendExecLog.message,
                localAt = appendExecLog.localAt
            )
        )
        return ResponseEntity.created(URI("/")).build()
    }


}

@Serializable
data class AppendExecLogCmd(
    val level: ExecLogLevel,
    val message: LogMessage,
    val localAt: LocalAt
)
