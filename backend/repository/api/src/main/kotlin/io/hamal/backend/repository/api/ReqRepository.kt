package io.hamal.backend.repository.api

import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.req.SubmittedReq

interface ReqCmdRepository {
    fun queue(req: SubmittedReq)
    fun next(limit: Int): List<SubmittedReq>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
    fun clear()
}

interface ReqQueryRepository {
    fun find(reqId: ReqId): SubmittedReq?
    fun list(block: Query.() -> Unit): List<SubmittedReq>
    data class Query(
        var afterId: ReqId,
        var limit: Int
    )
}