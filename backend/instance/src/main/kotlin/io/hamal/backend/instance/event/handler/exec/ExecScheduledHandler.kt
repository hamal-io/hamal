package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecScheduledEvent
import io.hamal.backend.instance.event.ExecutionQueuedEvent
import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.QueuedExec

class ExecScheduledHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: SystemEventEmitter<*>
) : SystemEventHandler<ExecScheduledEvent> {
    override fun handle(cmdId: CmdId, evt: ExecScheduledEvent) {
        queue(cmdId, evt).also { emitEvent(cmdId, it) }
    }

    private fun queue(cmdId: CmdId, evt: ExecScheduledEvent): QueuedExec {
        return execCmdRepository.queue(ExecCmdRepository.QueueCmd(cmdId, evt.scheduledExec.id))
    }

    private fun emitEvent(cmdId: CmdId, exec: QueuedExec) {
        eventEmitter.emit(cmdId, ExecutionQueuedEvent(exec))
    }
}