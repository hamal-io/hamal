package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecutionCompletedEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService
) : SystemEventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
    }
}