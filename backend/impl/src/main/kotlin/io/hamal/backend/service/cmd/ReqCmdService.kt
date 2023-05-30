package io.hamal.backend.service.cmd

import io.hamal.backend.event.RequestedEvent
import io.hamal.backend.event.component.EventEmitter
import io.hamal.backend.repository.api.domain.Req
import io.hamal.backend.repository.api.domain.ReqPayload
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReqCmdService
@Autowired constructor(
    val eventEmitter: EventEmitter,
    val execCmdService: ExecCmdService
) {

    fun request(payload: ReqPayload): Req {
        return Req(
            id = ReqId(1),
            payload = payload
        ).also {
            eventEmitter.emit(
                RequestedEvent(
                    shard = Shard(1),
                    req = it
                )
            )
        }
    }

    // requested
    // complete req
    // fail req

}