package io.hamal.core.event.handler.exec

import io.hamal.core.event.PlatformEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.event.ExecQueuedEvent

private val log = logger(ExecQueuedHandler::class)

class ExecQueuedHandler : PlatformEventHandler<ExecQueuedEvent> {
    override fun handle(cmdId: CmdId, evt: ExecQueuedEvent) {
        log.debug("Handle: $evt")
    }
}