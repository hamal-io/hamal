package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.HubEventHandler
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.backend.service.MetricService
import io.hamal.backend.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecCompletedHandler::class)

class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService,
    private val metricService: MetricService
) : HubEventHandler<ExecutionCompletedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
        metricService.handleEvent(evt)
    }
}