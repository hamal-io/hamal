package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.Req
import io.hamal.backend.repository.api.domain.ReqPayload
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.ReqId

interface ReqCmdRepository {
    fun queue(toQueue: ToQueue): Req
    fun dequeue(limit: Int): List<Req>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)

    data class ToQueue(
        val id: ReqId,
        val shard: Shard,
        val payload: ReqPayload
    )
}

interface ReqQueryRepository {
    fun find(reqId: ReqId): Req?
    fun list(afterId: ReqId, limit: Int): List<Req>
}