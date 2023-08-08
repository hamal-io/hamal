package io.hamal.backend.repository.memory

import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import java.util.concurrent.ConcurrentHashMap

object MemoryStateRepository : StateCmdRepository, StateQueryRepository {
    private val states = ConcurrentHashMap<Correlation, CorrelatedState>()

    override fun set(cmdId: CmdId, correlatedState: CorrelatedState) {
        states[correlatedState.correlation] = correlatedState
    }

    override fun clear() {
        states.clear()
    }

    override fun find(correlation: Correlation) = states[correlation]

}