package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecutionQueuedEvent
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.lib.domain.CmdId
import logger

class ExecQueuedHandler : SystemEventHandler<ExecutionQueuedEvent> {
    private val log = logger(ExecQueuedHandler::class)
    override fun handle(cmdId: CmdId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}