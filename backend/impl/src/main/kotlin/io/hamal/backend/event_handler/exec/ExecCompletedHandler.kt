package io.hamal.backend.event_handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event_handler.EventHandler
import io.hamal.backend.service.OrchestrationService
import io.hamal.backend.service.cmd.ReqCmdService
import logger

class ExecCompletedHandler(
    val orchestrationService: OrchestrationService,
    val reqCmdService: ReqCmdService
) : EventHandler<ExecutionCompletedEvent> {

    private val log = logger(ExecCompletedHandler::class)
    override fun handle(evt: ExecutionCompletedEvent) {
        log.debug("Handle: $evt")

        orchestrationService.completed(
            reqId = evt.reqId,
            completedExec = evt.completedExec
        )
        reqCmdService.complete(evt.reqId)
        reqCmdService.complete(evt.completedExec.reqId)
        /* FIXME seems rather tedious to complete each request manually - is it possible to have some form
        *   parent child relationship so if one fails / completes the whole chain gets set ?!
        */
    }
}