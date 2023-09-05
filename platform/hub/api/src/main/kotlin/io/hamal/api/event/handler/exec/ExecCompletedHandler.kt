package io.hamal.api.event.handler.exec

import io.hamal.api.event.HubEventHandler
import io.hamal.repository.api.event.ExecutionCompletedEvent
import io.hamal.api.service.MetricService
import io.hamal.api.service.OrchestrationService
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