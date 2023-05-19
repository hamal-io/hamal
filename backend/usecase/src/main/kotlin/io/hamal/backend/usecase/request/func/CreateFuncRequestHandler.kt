package io.hamal.backend.usecase.request.func

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.notification.FuncCreatedNotification
import io.hamal.backend.core.notification.ManualTriggerCreatedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.createManualTrigger
import io.hamal.backend.usecase.request.FuncRequest.FuncCreation
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.FuncRef
import io.hamal.lib.domain.vo.TriggerRef

class CreateFuncRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val funcRepository: FuncRepository
) : RequestOneUseCaseHandler<Func, FuncCreation>(FuncCreation::class) {
    override fun invoke(useCase: FuncCreation): Func {
        val result = createFunc(useCase)
        notifyDefinitionCreated(result)
        notifyTriggersCreated(result)
        return result
    }
}

internal fun CreateFuncRequestHandler.createFunc(useCase: FuncCreation): Func {
    return funcRepository.request(useCase.reqId) {
        val funcId = createFunc {
            ref = FuncRef("ref")
            code = Code(
                """
                        |require('eth')
                    """.trimMargin()
            )
        }
        createManualTrigger(funcId) {
            reference = TriggerRef("manual")
        }
    }.first()
}

internal fun CreateFuncRequestHandler.notifyDefinitionCreated(func: Func) {
    notifyDomain(
        FuncCreatedNotification(
            shard = func.shard,
            id = func.id
        )
    )
}

internal fun CreateFuncRequestHandler.notifyTriggersCreated(func: Func) {
    func.triggers.forEach { trigger ->
        notifyDomain.invoke(
            ManualTriggerCreatedNotification(
                shard = func.shard,
                id = trigger.id
            )
        )
    }
}