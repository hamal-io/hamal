package io.hamal.backend.event.handler.exec

import io.hamal.backend.event.event.ExecutionQueuedEvent
import io.hamal.backend.event.HubEventHandler
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecQueuedHandler::class)

class ExecQueuedHandler : HubEventHandler<ExecutionQueuedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}