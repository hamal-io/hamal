package io.hamal.backend.cmd.handler.exec

import io.hamal.backend.event.ExecutionQueuedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.repository.api.domain.exec.QueuedExec
import io.hamal.backend.cmd.ExecCmd.QueueScheduledExec
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import logger

class QueueScheduledExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<QueuedExec, QueueScheduledExec>(useCaseClass = QueueScheduledExec::class) {
    private val log = logger(QueueScheduledExecRequestHandler::class)
    override fun invoke(useCase: QueueScheduledExec): QueuedExec {
        log.debug("Queue scheduled exec: ${useCase.scheduledExec}")
        val result = execRequestRepository.queue(useCase.reqId, useCase.scheduledExec)
        eventEmitter.emit(
            ExecutionQueuedEvent(
                shard = useCase.shard,
                queuedExec = result
            )
        )

        return result
    }
}