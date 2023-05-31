package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.Req
import io.hamal.lib.domain.ComputeId

interface ReqCmdRepository {
    fun inFlight(req: Req)
    fun complete(computeId: ComputeId)
    fun fail(computeId: ComputeId)
    data class ToInsert(
        val computeId: ComputeId
    )
}

interface ReqQueryRepository {
    fun find(computeId: ComputeId): Req?
    fun list(afterId: ComputeId, limit: Int): List<Req>
}