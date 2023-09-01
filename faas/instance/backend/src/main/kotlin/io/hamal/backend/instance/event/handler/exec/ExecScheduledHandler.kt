package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.InstanceEventHandler
import io.hamal.backend.instance.event.event.ExecScheduledEvent
import io.hamal.backend.instance.event.event.ExecutionQueuedEvent
import io.hamal.lib.common.domain.CmdId
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.QueueCmd
import io.hamal.repository.api.QueuedExec

class ExecScheduledHandler(
    private val execCmdRepository: ExecCmdRepository,
    private val eventEmitter: InstanceEventEmitter
) : InstanceEventHandler<ExecScheduledEvent> {

    override fun handle(cmdId: CmdId, evt: ExecScheduledEvent) {
        queue(cmdId, evt).also { emitEvent(cmdId, it) }
    }

    private fun queue(cmdId: CmdId, evt: ExecScheduledEvent): QueuedExec {
        return execCmdRepository.queue(QueueCmd(cmdId, evt.scheduledExec.id))
    }

    private fun emitEvent(cmdId: CmdId, exec: QueuedExec) {
        eventEmitter.emit(cmdId, ExecutionQueuedEvent(exec))
    }
}