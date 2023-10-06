package io.hamal.repository.api

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State

interface StateRepository : StateCmdRepository, StateQueryRepository

interface StateCmdRepository : CmdRepository {
    fun set(cmdId: CmdId, correlatedState: CorrelatedState)
}

interface StateQueryRepository {
    fun find(correlation: Correlation): CorrelatedState?
    fun get(correlation: Correlation) = find(correlation)
        ?: CorrelatedState(correlation, State())
}