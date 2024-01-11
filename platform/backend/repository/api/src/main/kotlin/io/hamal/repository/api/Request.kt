package io.hamal.repository.api

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId

interface RequestRepository : RequestCmdRepository, RequestQueryRepository

interface RequestCmdRepository : CmdRepository {
    fun queue(req: Requested)
    fun next(limit: Int): List<Requested>
    fun complete(reqId: RequestId)
    fun fail(reqId: RequestId)
}

interface RequestQueryRepository {
    fun get(reqId: RequestId) = find(reqId) ?: throw NoSuchElementException("Req not found")
    fun find(reqId: RequestId): Requested?
    fun list(query: ReqQuery): List<Requested>
    fun count(query: ReqQuery): ULong
    data class ReqQuery(
        var afterId: RequestId = RequestId(0),
        var limit: Limit = Limit(1)
    )
}