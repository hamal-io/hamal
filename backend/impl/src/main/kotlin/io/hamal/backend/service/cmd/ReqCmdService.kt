package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.ReqCmdRepository
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
    val reqCmdRepository: ReqCmdRepository,
    val eventEmitter: EventEmitter
) {
    fun request(payload: ReqPayload): Req {
        return reqCmdRepository.queue(
            ReqCmdRepository.ToQueue(
                id = ReqId(BigInteger(128, SecureRandom())),
                shard = Shard(1),
                payload = payload
            )
        )
    }
}