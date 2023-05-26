package io.hamal.backend.service.cmd

import io.hamal.backend.repository.api.domain.trigger.AdhocInvocation
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdhocCmdService
@Autowired constructor(
    val execCmdService: ExecCmdService
) {

    fun execute(adhocToExecute: AdhocToExecute) {
//        eventEmitter.emit(
//            AdhocTriggerInvokedEvent(
//                shard = adhocToExecute.shard,
//                adhocTrigger = AdhocTrigger(
//                    id = InvokedTriggerId(SnowflakeId(0)), // FIXME
//                    funcId = FuncId(0),//FIXME
//                    invokedAt = InvokedAt.now(),
//                    invokedBy = Requester.tenant(TenantId(12)), //FIXME
//                )
//            )
//        )
        execCmdService.plan(
            ExecCmdService.ToPlan(
                reqId = adhocToExecute.reqId,
                shard = adhocToExecute.shard,
                code = adhocToExecute.code,
                invocation = AdhocInvocation()
            )
        )

    }

    data class AdhocToExecute(
        val reqId: ReqId,
        val shard: Shard,
        val code: Code
    )
}