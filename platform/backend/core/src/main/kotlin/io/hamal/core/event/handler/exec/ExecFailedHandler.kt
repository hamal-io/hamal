package io.hamal.core.event.handler.exec

import io.hamal.core.event.PlatformEventHandler
import io.hamal.core.service.OrchestrationService
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.logger
import io.hamal.repository.api.event.ExecFailedEvent

private val log = logger(ExecFailedHandler::class)

internal class ExecFailedHandler(
    private val orchestrationService: OrchestrationService
) : PlatformEventHandler<ExecFailedEvent> {

    override fun handle(cmdId: CmdId, evt: ExecFailedEvent) {
        log.debug("Handle: $evt")
        orchestrationService.failed(cmdId, evt.failedExec)
    }
}