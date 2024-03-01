package io.hamal.repository.memory

import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.repository.api.StateCmdRepository.SetCmd
import io.hamal.repository.api.StateRepository
import java.util.concurrent.ConcurrentHashMap

class StateMemoryRepository : StateRepository {
    private val states = ConcurrentHashMap<Correlation, CorrelatedState>()

    override fun set(cmd: SetCmd) {
        states[cmd.correlatedState.correlation] = cmd.correlatedState
    }

    override fun clear() {
        states.clear()
    }

    override fun close() {
    }

    override fun find(correlation: Correlation) = states[correlation]

}