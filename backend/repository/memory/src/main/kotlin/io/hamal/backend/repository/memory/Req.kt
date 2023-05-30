package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.domain.CompletedReq
import io.hamal.backend.repository.api.domain.FailedReq
import io.hamal.backend.repository.api.domain.InFlightReq
import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ReqId

object MemoryReqRepository : ReqCmdRepository, ReqQueryRepository {

    val reqs = mutableMapOf<ReqId, Req>() // FIXME  have most recent version in here

    override fun inFlight(req: Req) {
        reqs[req.id] = InFlightReq(
            id = req.id,
            payload = req.payload
        )
    }

    override fun complete(reqId: ReqId) {
        check(reqs.containsKey(reqId))

        val r = reqs[reqId]!!

        reqs[reqId] = CompletedReq(
            id = r.id,
            payload = r.payload
        )

    }

    override fun fail(reqId: ReqId) {
        check(reqs.containsKey(reqId))

        val r = reqs[reqId]!!

        reqs[reqId] = FailedReq(
            id = r.id,
            payload = r.payload
        )
    }

    override fun find(reqId: ReqId) = reqs[reqId]

    override fun list(afterId: ReqId, limit: Int): List<Req> {
        return reqs.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
    }
}