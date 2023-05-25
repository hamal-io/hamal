package io.hamal.backend.cmd.handler.exec

import io.hamal.backend.event.ExecutionStartedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.domain.exec.StartedExec
import io.hamal.backend.cmd.ExecCmd
import io.hamal.lib.domain.ddd.RequestManyUseCaseHandler
import logger

class DequeueExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execCmdRepository: ExecCmdRepository
) : RequestManyUseCaseHandler<StartedExec, ExecCmd.DequeueExec>(ExecCmd.DequeueExec::class) {
    private val log = logger(DequeueExecRequestHandler::class)
    override fun invoke(useCase: ExecCmd.DequeueExec): List<StartedExec> {
        log.debug("Dequeue exec")
        val result = execCmdRepository.dequeue()

        result.forEach {
            eventEmitter.emit(
                ExecutionStartedEvent(
                    shard = useCase.shard,
                    startedExec = it
                )
            )
        }
        return result
    }
}