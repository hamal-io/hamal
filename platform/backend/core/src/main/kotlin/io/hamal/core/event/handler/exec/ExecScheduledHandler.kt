package io.hamal.core.event.handler.exec

import io.hamal.core.event.InternalEventEmitter
import io.hamal.core.event.InternalEventHandler
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.QueueCmd
import io.hamal.repository.api.event.ExecQueuedEvent
import io.hamal.repository.api.event.ExecScheduledEvent
import org.springframework.stereotype.Component

@Component
class ExecScheduledHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: InternalEventEmitter
) : InternalEventHandler<ExecScheduledEvent> {

    override fun handle(cmdId: CmdId, evt: ExecScheduledEvent) {
        queue(cmdId, evt).also { emitEvent(cmdId, it) }
    }

    private fun queue(cmdId: CmdId, evt: ExecScheduledEvent): Exec.Queued {
        return execCmdRepository.queue(QueueCmd(cmdId, evt.scheduledExec.id))
    }

    private fun emitEvent(cmdId: CmdId, exec: Exec.Queued) {
        eventEmitter.emit(cmdId, ExecQueuedEvent(exec))
    }
}