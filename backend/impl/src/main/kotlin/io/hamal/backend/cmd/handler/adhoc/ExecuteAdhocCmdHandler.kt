package io.hamal.backend.cmd.handler.adhoc

import io.hamal.backend.cmd.AdhocCmd.ExecuteAdhoc
import io.hamal.backend.event.AdhocTriggerInvokedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.FuncRequestRepository
import io.hamal.backend.repository.api.createFunc
import io.hamal.backend.repository.api.domain.func.Func
import io.hamal.backend.repository.api.domain.trigger.AdhocTrigger
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.ddd.RequestOneUseCaseHandler
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.InvokedAt
import io.hamal.lib.domain.vo.InvokedTriggerId
import io.hamal.lib.domain.vo.TenantId

class ExecuteAdhocRequestHandler(
    internal val eventEmitter: EventEmitter,
    internal val funcRepository: FuncRequestRepository
) : RequestOneUseCaseHandler<Func, ExecuteAdhoc>(ExecuteAdhoc::class) {
    override fun invoke(useCase: ExecuteAdhoc): Func {
        //FIXME just a quick hack - job definition is not really required ?!
        val result = createFunc(useCase)
        eventEmitter.emit(
            AdhocTriggerInvokedEvent(
                shard = useCase.shard,
                adhocTrigger = AdhocTrigger(
                    id = InvokedTriggerId(SnowflakeId(0)), // FIXME
                    invokedAt = InvokedAt.now(),
                    invokedBy = Requester.tenant(TenantId(12)), //FIXME
                    code = useCase.code
                )
            )
        )
        return result
    }
}

internal fun ExecuteAdhocRequestHandler.createFunc(useCase: ExecuteAdhoc): Func {
    return funcRepository.request(useCase.reqId) {
        val funcId = createFunc {
            name = FuncName("func-ref")
            code = useCase.code
        }
    }.first()
}

