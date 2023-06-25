package io.hamal.backend.repository.api

import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State

interface StateCmdRepository {
    fun set(cmdId: CmdId, stateToSet: StateToSet): CorrelatedState
    data class StateToSet(
        val correlation: Correlation,
        val state: State
    )
}

interface StateQueryRepository {
    fun find(correlation: Correlation): CorrelatedState?
}