package io.hamal.core.event.handler.exec

import io.hamal.core.event.InternalEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.AuthCmdRepository.RevokeAuthCmd
import io.hamal.repository.api.AuthRepository
import io.hamal.repository.api.event.ExecCompletedEvent
import org.springframework.stereotype.Component

private val log = logger(ExecCompletedHandler::class)

@Component
internal class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService,
    private val authRepository: AuthRepository
) : InternalEventHandler<ExecCompletedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)

        authRepository.find(evt.completedExec.id)?.also { auth ->
            authRepository.revokeAuth(
                RevokeAuthCmd(
                    id = cmdId,
                    authId = auth.id
                )
            )
        }
    }
}