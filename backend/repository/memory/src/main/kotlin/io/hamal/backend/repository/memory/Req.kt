package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.ReqCmdRepository
import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.domain.CompletedReq
import io.hamal.backend.repository.api.domain.FailedReq
import io.hamal.backend.repository.api.domain.InFlightReq
import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ComputeId

object MemoryReqRepository : ReqCmdRepository, ReqQueryRepository {

    val reqs = mutableMapOf<ComputeId, Req>() // FIXME  have most recent version in here

    override fun inFlight(req: Req) {
        reqs[req.id] = InFlightReq(
            id = req.id,
            payload = req.payload
        )
    }

    override fun complete(computeId: ComputeId) {
        check(reqs.containsKey(computeId))

        val r = reqs[computeId]!!

        reqs[computeId] = CompletedReq(
            id = r.id,
            payload = r.payload
        )

    }

    override fun fail(computeId: ComputeId) {
        check(reqs.containsKey(computeId))

        val r = reqs[computeId]!!

        reqs[computeId] = FailedReq(
            id = r.id,
            payload = r.payload
        )
    }

    override fun find(computeId: ComputeId) = reqs[computeId]

    override fun list(afterId: ComputeId, limit: Int): List<Req> {
        return reqs.keys.sorted()
            .dropWhile { it <= afterId }
            .take(limit)
            .mapNotNull { find(it) }
            .reversed()
    }
}