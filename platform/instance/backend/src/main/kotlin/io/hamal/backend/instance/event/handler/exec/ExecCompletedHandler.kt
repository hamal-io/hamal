package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.service.MetricService
import io.hamal.backend.instance.event.event.ExecutionCompletedEvent
import io.hamal.backend.instance.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecCompletedHandler::class)

class ExecCompletedHandler(
    private val orchestrationService: OrchestrationService,
    private val metricService: MetricService
) : InstanceEventHandler<ExecutionCompletedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.completed(cmdId, evt.completedExec)
        metricService.handleEvent(evt)
    }
}