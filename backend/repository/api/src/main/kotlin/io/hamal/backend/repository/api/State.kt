package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.State
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId

interface StateCmdRepository {
    fun set(reqId: ReqId, stateToSet: StateToSet): State
    data class StateToSet(
        val correlation: Correlation,
        val contentType: String,
        val bytes: ByteArray
    )
}

interface StateQueryRepository {
    fun find(correlation: Correlation): State?
}