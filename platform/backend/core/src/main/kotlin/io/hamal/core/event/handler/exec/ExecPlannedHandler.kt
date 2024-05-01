package io.hamal.core.event.handler.exec

import io.hamal.core.event.InternalEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.repository.api.AuthCmdRepository.CreateExecTokenAuthCmd
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.event.ExecPlannedEvent
import org.springframework.stereotype.Component
import java.util.*

private val log = logger(ExecPlannedHandler::class)

@Component
internal class ExecPlannedHandler(
    private val orchestrationService: OrchestrationService,
    private val authRepository: AuthRepository,
    private val generateDomainId: GenerateDomainId
) : InternalEventHandler<ExecPlannedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecPlannedEvent) {
        log.debug("Handle: $evt")
        authRepository.create(
            CreateExecTokenAuthCmd(
                id = cmdId,
                authId = generateDomainId(::AuthId),
                token = ExecToken(UUID.randomUUID().toString()),
                execId = evt.plannedExec.id
            )
        )
        orchestrationService.schedule(cmdId, evt.plannedExec)
    }
}