package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ReqId

interface ReqCmdRepository {
    fun inFlight(req: Req)
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
    data class ToInsert(
        val reqId: ReqId
    )
}

interface ReqQueryRepository {
    fun find(reqId: ReqId): Req?
    fun list(afterId: ReqId, limit: Int): List<Req>
}