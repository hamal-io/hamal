package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ReqId

interface ReqCmdRepository {
    fun queue(req: Req)
    fun next(limit: Int): List<Req>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
}

interface ReqQueryRepository {
    fun find(reqId: ReqId): Req?
    fun list(afterId: ReqId, limit: Int): List<Req>
}