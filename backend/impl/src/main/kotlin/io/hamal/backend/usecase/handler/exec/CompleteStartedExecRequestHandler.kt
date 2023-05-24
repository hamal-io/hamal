package io.hamal.backend.usecase.handler.exec

import io.hamal.backend.event.ExecutionCompletedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.repository.api.domain.exec.CompleteExec
import io.hamal.backend.usecase.ExecRequestUseCase
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import logger

class CompleteStartedExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execRequestRepository: ExecRequestRepository
) : RequestOneUseCaseHandler<CompleteExec, ExecRequestUseCase.CompleteStartedExec>(ExecRequestUseCase.CompleteStartedExec::class) {
    private val log = logger(CompleteStartedExecRequestHandler::class)
    override fun invoke(useCase: ExecRequestUseCase.CompleteStartedExec): CompleteExec {
        log.debug("Complete started exec: ${useCase.startedExec}")
        val result = execRequestRepository.complete(useCase.reqId, useCase.startedExec)
        eventEmitter.emit(
            ExecutionCompletedEvent(
                useCase.shard,
                result
            )
        )
        return result
    }

}