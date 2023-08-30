package io.hamal.backend.instance.event.handler.exec

import io.hamal.backend.instance.event.ExecScheduledEvent
import io.hamal.backend.instance.event.ExecutionQueuedEvent
import io.hamal.backend.instance.event.InstanceEventEmitter
import io.hamal.backend.instance.event.handler.SystemEventHandler
import io.hamal.lib.common.domain.CmdId

class ExecScheduledHandler(
    private val execCmdRepository: io.hamal.repository.api.ExecCmdRepository,
    private val eventEmitter: InstanceEventEmitter
) : SystemEventHandler<ExecScheduledEvent> {
    override fun handle(cmdId: CmdId, evt: ExecScheduledEvent) {
        queue(cmdId, evt).also { emitEvent(cmdId, it) }
    }

    private fun queue(cmdId: CmdId, evt: ExecScheduledEvent): io.hamal.repository.api.QueuedExec {
        return execCmdRepository.queue(io.hamal.repository.api.ExecCmdRepository.QueueCmd(cmdId, evt.scheduledExec.id))
    }

    private fun emitEvent(cmdId: CmdId, exec: io.hamal.repository.api.QueuedExec) {
        eventEmitter.emit(cmdId, ExecutionQueuedEvent(exec))
    }
}