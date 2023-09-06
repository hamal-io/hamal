package io.hamal.core.event.handler.exec

import io.hamal.core.event.HubEventHandler
import io.hamal.core.service.MetricService
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.event.ExecutionCompletedEvent
import logger

private val log = logger(ExecCompletedHandler::class)

internal class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService,
    private val metricService: MetricService
) : HubEventHandler<ExecutionCompletedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
        metricService.handleEvent(evt)
    }
}