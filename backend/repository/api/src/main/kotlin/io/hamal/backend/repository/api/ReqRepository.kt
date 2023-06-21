package io.hamal.backend.repository.api

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.Req

interface ReqCmdRepository {
    fun queue(req: Req)
    fun next(limit: Int): List<Req>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
}

interface ReqQueryRepository {
    fun find(reqId: ReqId): Req?
    fun query(block: Query.() -> Unit): List<Req>
    data class Query(
        var afterId: ReqId,
        var limit: Int
    )
}