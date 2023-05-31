package io.hamal.backend.repository.api

import io.hamal.backend.repository.api.domain.State
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ComputeId
import io.hamal.lib.domain.StatePayload

interface StateCmdRepository {
    fun set(computeId: ComputeId, stateToSet: StateToSet): State
    data class StateToSet(
        val correlation: Correlation,
        val payload: StatePayload
    )
}

interface StateQueryRepository {
    fun find(correlation: Correlation): State?
}