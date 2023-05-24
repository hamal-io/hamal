package io.hamal.backend.usecase.handler.func

import io.hamal.backend.event.FuncCreatedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.usecase.FuncRequestUseCase.FuncCreation
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CreateFuncRequestHandler(
    internal val eventEmitter: EventEmitter,
    internal val funcRepository: FuncRequestRepository
) : RequestOneUseCaseHandler<Func, FuncCreation>(FuncCreation::class) {
    override fun invoke(useCase: FuncCreation): Func {
        val result = createFunc(useCase)
        notifyFuncCreated(result)
//        notifyTriggersCreated(result)
        return result
    }
}

internal fun CreateFuncRequestHandler.createFunc(useCase: FuncCreation): Func {
    return funcRepository.request(useCase.reqId) {
        createFunc {
            name = useCase.name
            code = useCase.code
        }
    }.first()
}

internal fun CreateFuncRequestHandler.notifyFuncCreated(func: Func) {
    eventEmitter.emit(
        FuncCreatedEvent(
            shard = func.shard,
            id = func.id
        )
    )
}
