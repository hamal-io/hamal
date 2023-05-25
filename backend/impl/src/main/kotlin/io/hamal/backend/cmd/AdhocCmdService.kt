package io.hamal.backend.cmd

import io.hamal.backend.event.AdhocTriggerInvokedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.ExecCmdRepository
import io.hamal.backend.repository.api.domain.trigger.AdhocTrigger
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.Requester
import io.hamal.lib.domain.Shard
import io.hamal.lib.domain.vo.Code
import io.hamal.lib.domain.vo.InvokedAt
import io.hamal.lib.domain.vo.InvokedTriggerId
import io.hamal.lib.domain.vo.TenantId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdhocCmdService
@Autowired constructor(
    val execCmdRepository: ExecCmdRepository,
    val eventEmitter: EventEmitter
) {

    fun execute(adhocToExecute: AdhocToExecute) {
        eventEmitter.emit(
            AdhocTriggerInvokedEvent(
                shard = adhocToExecute.shard,
                adhocTrigger = AdhocTrigger(
                    id = InvokedTriggerId(SnowflakeId(0)), // FIXME
                    invokedAt = InvokedAt.now(),
                    invokedBy = Requester.tenant(TenantId(12)), //FIXME
                    code = adhocToExecute.code
                )
            )
        )
    }

    data class AdhocToExecute(
        val reqId: ReqId,
        val shard: Shard,
        val code: Code
    )
}