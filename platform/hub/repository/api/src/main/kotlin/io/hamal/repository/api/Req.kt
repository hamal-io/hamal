package io.hamal.repository.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.ReqId
import io.hamal.repository.api.submitted_req.SubmittedReq

interface ReqRepository : ReqCmdRepository, ReqQueryRepository

interface ReqCmdRepository : CmdRepository {
    fun queue(req: SubmittedReq)
    fun next(limit: Int): List<SubmittedReq>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
}

interface ReqQueryRepository {
    fun get(reqId: ReqId) = find(reqId) ?: throw NoSuchElementException("Req not found")
    fun find(reqId: ReqId): SubmittedReq?
    fun list(query: ReqQuery): List<SubmittedReq>
    fun count(query: ReqQuery): ULong
    data class ReqQuery(
        var afterId: ReqId = ReqId(0),
        var limit: Limit = Limit(1)
    )
}