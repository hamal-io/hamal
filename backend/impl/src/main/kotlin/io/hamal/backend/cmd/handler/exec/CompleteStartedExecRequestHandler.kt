package io.hamal.backend.cmd.handler.exec

import io.hamal.backend.cmd.ExecCmd
import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.domain.exec.CompletedExec
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import logger

class CompleteStartedExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execCmdRepository: ExecCmdRepository
) : RequestOneUseCaseHandler<CompletedExec, ExecCmd.CompleteStartedExec>(ExecCmd.CompleteStartedExec::class) {
    private val log = logger(CompleteStartedExecRequestHandler::class)
    override fun invoke(useCase: ExecCmd.CompleteStartedExec): CompletedExec {
        log.debug("Complete started exec: ${useCase.startedExec}")
        val result = execCmdRepository.complete(useCase.reqId, useCase.startedExec)
        eventEmitter.emit(
            ExecutionCompletedEvent(
                useCase.shard,
                result
            )
        )
        return result
    }

}