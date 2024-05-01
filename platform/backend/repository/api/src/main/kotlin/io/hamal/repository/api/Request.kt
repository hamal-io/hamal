package io.hamal.repository.api

import io.hamal.lib.common.domain.Count
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.request.Requested
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.domain.vo.RequestId.Companion.RequestId

interface RequestRepository : RequestCmdRepository, RequestQueryRepository

interface RequestCmdRepository : CmdRepository {
    fun queue(req: Requested)
    fun next(limit: Limit): List<Requested>
    fun complete(reqId: RequestId)
    fun fail(reqId: RequestId)
}

interface RequestQueryRepository {
    fun get(reqId: RequestId) = find(reqId) ?: throw NoSuchElementException("Request not found")
    fun find(reqId: RequestId): Requested?
    fun list(query: RequestQuery): List<Requested>
    fun count(query: RequestQuery): Count
    data class RequestQuery(
        var afterId: RequestId = RequestId(0),
        var limit: Limit = Limit(1)
    )
}