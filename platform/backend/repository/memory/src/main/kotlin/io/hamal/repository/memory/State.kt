package io.hamal.repository.memory

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import io.hamal.repository.api.StateRepository
import java.util.concurrent.ConcurrentHashMap

class MemoryStateRepository : StateRepository {
    private val states = ConcurrentHashMap<Correlation, CorrelatedState>()

    override fun set(cmdId: CmdId, correlatedState: CorrelatedState) {
        states[correlatedState.correlation] = correlatedState
    }

    override fun clear() {
        states.clear()
    }

    override fun close() {
    }

    override fun find(correlation: Correlation) = states[correlation]

}