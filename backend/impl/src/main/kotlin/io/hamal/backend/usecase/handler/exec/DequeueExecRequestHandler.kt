package io.hamal.backend.usecase.handler.exec

import io.hamal.backend.event.ExecutionStartedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecRequestRepository
import io.hamal.backend.repository.api.domain.exec.StartedExec
import io.hamal.backend.usecase.ExecRequestUseCase
import io.hamal.lib.domain.ddd.RequestManyUseCaseHandler
import logger

class DequeueExecRequestHandler(
    internal val eventEmitter: EventEmitter,
    val execRequestRepository: ExecRequestRepository
) : RequestManyUseCaseHandler<StartedExec, ExecRequestUseCase.DequeueExec>(ExecRequestUseCase.DequeueExec::class) {
    private val log = logger(DequeueExecRequestHandler::class)
    override fun invoke(useCase: ExecRequestUseCase.DequeueExec): List<StartedExec> {
        log.debug("Dequeue exec")
        val result = execRequestRepository.dequeue()

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