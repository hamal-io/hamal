package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.lib.domain.Correlation
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState

object MemoryStateRepository : StateCmdRepository, StateQueryRepository {

    private val states = mutableMapOf<Correlation, CorrelatedState>()

    override fun set(cmdId: CmdId, stateToSet: StateCmdRepository.StateToSet): CorrelatedState {
        return CorrelatedState(
            correlation = stateToSet.correlation,
            state = stateToSet.state,
        ).also { states[it.correlation] = it }
    }

    override fun find(correlation: Correlation) = states[correlation]

}