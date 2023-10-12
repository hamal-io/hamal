package io.hamal.core.event.handler.exec

import io.hamal.core.event.PlatformEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.event.ExecutionCompletedEvent

private val log = logger(ExecCompletedHandler::class)

internal class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService,
) : PlatformEventHandler<ExecutionCompletedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
    }
}