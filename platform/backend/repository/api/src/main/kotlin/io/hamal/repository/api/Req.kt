package io.hamal.repository.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ReqId
import io.hamal.lib.domain.submitted.Submitted

interface ReqRepository : ReqCmdRepository, ReqQueryRepository

interface ReqCmdRepository : CmdRepository {
    fun queue(req: Submitted)
    fun next(limit: Int): List<Submitted>
    fun complete(reqId: ReqId)
    fun fail(reqId: ReqId)
}

interface ReqQueryRepository {
    fun get(reqId: ReqId) = find(reqId) ?: throw NoSuchElementException("Req not found")
    fun find(reqId: ReqId): Submitted?
    fun list(query: ReqQuery): List<Submitted>
    fun count(query: ReqQuery): ULong
    data class ReqQuery(
        var afterId: ReqId = ReqId(0),
        var limit: Limit = Limit(1)
    )
}