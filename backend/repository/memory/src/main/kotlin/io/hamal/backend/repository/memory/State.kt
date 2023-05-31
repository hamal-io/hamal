package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.backend.repository.api.domain.State
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ComputeId

object MemoryStateRepository : StateCmdRepository, StateQueryRepository {

    private val states = mutableMapOf<Correlation, State>()

    override fun set(computeId: ComputeId, stateToSet: StateCmdRepository.StateToSet): State {
        return State(
            correlation = stateToSet.correlation,
            payload = stateToSet.payload,
        ).also { states[it.correlation] = it }
    }

    override fun find(correlation: Correlation) = states[correlation]

}