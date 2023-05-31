package io.hamal.backend.service.cmd

import io.hamal.backend.event.RequestedEvent
import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.domain.ReceivedReq
import io.hamal.backend.repository.api.domain.Req
import io.hamal.backend.repository.api.domain.ReqPayload
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.SecureRandom

@Service
class ReqCmdService
@Autowired constructor(
    val eventEmitter: EventEmitter,
    val reqCmdRepository: ReqCmdRepository
) {

    // FIXME probably move this into a Requester component because request() should be the only function used to deal with requests
    fun request(payload: ReqPayload): Req {
        return ReceivedReq(
            id = ReqId(BigInteger(128, SecureRandom())), //FIXME depends on the req, usually generate hash of payload
            payload = payload
        ).also {
            eventEmitter.emit(
                RequestedEvent(
//                    reqId = it.id,
                    shard = Shard(1),
                    req = it
                )
            )
        }
    }

    // requested
    // complete req
    // fail req


    fun inflight(req: Req) {
        reqCmdRepository.inFlight(req)
    }

    fun complete(reqId: ReqId) {
        reqCmdRepository.complete(reqId)
    }

    fun fail(reqId: ReqId) {
        reqCmdRepository.fail(reqId)
    }
}