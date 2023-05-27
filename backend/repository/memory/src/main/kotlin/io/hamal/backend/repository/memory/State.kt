package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.backend.repository.api.domain.State
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.ReqId

object MemoryStateRepository : StateCmdRepository, StateQueryRepository {

    private val states = mutableMapOf<Correlation, State>()

    override fun set(reqId: ReqId, stateToSet: StateCmdRepository.StateToSet): State {
        return State(
            correlation = stateToSet.correlation,
            contentType = stateToSet.contentType,
            bytes = stateToSet.bytes
        ).also { states[it.correlation] = it }
    }

    override fun find(correlation: Correlation) = states[correlation]

}