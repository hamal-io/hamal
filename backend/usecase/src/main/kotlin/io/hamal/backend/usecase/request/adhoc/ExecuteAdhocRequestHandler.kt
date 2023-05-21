package io.hamal.backend.usecase.request.adhoc

import io.hamal.backend.core.func.Func
import io.hamal.backend.core.notification.AdhocTriggerInvokedNotification
import io.hamal.backend.core.notification.port.NotifyDomainPort
import io.hamal.backend.core.trigger.Cause
import io.hamal.backend.core.trigger.Trigger
import io.hamal.backend.repository.api.FuncRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.createManualTrigger
import io.hamal.backend.usecase.request.AdhocRequest.ExecuteAdhoc
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.*

class ExecuteAdhocRequestHandler(
    internal val notifyDomain: NotifyDomainPort,
    internal val funcRepository: FuncRepository
) : RequestOneUseCaseHandler<Func, ExecuteAdhoc>(ExecuteAdhoc::class) {
    override fun invoke(useCase: ExecuteAdhoc): Func {
        //FIXME just a quick hack - job definition is not really required ?!
        val result = createFunc(useCase)
        notifyDomain(
            AdhocTriggerInvokedNotification(
                shard = useCase.shard,
                cause = Cause.Adhoc(
                    id = CauseId(0),
                    func = result,
                    trigger = Trigger.AdhocTrigger(
                        id = TriggerId(1),
                        reference = TriggerRef("adhoc"),
                        funcId = result.id
                    ),
                    invokedAt = InvokedAt.now(),
                    invokedBy = Requester.tenant(TenantId(12))
                )
            )
        )
        return result
    }
}

internal fun ExecuteAdhocRequestHandler.createFunc(useCase: ExecuteAdhoc): Func {
    return funcRepository.request(useCase.reqId) {
        val funcId = createFunc {
            ref = FuncRef("func-ref")
            code = useCase.code
        }
        createManualTrigger(funcId) {
            reference = TriggerRef("manual")
        }
    }.first()
}

