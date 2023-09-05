package io.hamal.api.event.handler.exec

import io.hamal.api.event.HubEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.event.ExecutionQueuedEvent
import logger

private val log = logger(ExecQueuedHandler::class)

class ExecQueuedHandler : HubEventHandler<ExecutionQueuedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}