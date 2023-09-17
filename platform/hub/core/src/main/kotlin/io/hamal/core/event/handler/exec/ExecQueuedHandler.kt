package io.hamal.core.event.handler.exec

import io.hamal.core.event.HubEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.event.ExecutionQueuedEvent

private val log = logger(ExecQueuedHandler::class)

class ExecQueuedHandler : HubEventHandler<ExecutionQueuedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecutionQueuedEvent) {
        log.debug("Handle: $evt")
    }
}