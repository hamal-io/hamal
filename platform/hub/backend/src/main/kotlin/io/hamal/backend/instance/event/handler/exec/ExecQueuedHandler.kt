package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.event.ExecutionQueuedEvent
import io.hamal.lib.common.domain.CmdId
import logger

private val log = logger(ExecQueuedHandler::class)

class ExecQueuedHandler : InstanceEventHandler<ExecutionQueuedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}