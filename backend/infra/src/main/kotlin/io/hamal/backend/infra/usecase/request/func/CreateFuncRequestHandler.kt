package io.hamal.backend.infra.usecase.request.func

import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.infra.notification.FuncCreatedNotification
import io.hamal.backend.infra.usecase.request.FuncRequest.FuncCreation
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler

class CreateFuncRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
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
    notifyDomain(
        FuncCreatedNotification(
            shard = func.shard,
            id = func.id
        )
    )
}
