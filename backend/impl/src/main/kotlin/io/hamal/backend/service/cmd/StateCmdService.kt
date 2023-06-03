package io.hamal.backend.service.cmd

import io.hamal.backend.component.EventEmitter
import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.backend.repository.api.domain.State
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.CommandId
import io.hamal.lib.common.Shard
import io.hamal.lib.domain.StatePayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StateCmdService
@Autowired constructor(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: EventEmitter
) {

    fun set(commandId: CommandId, stateToSet: StateToSet): State {
        return stateCmdRepository.set(
            commandId, StateCmdRepository.StateToSet(
                correlation = stateToSet.correlation,
                payload = stateToSet.payload,
            )
        )
        //FIXME emit event
    }

    data class StateToSet(
        val shard: Shard,
        val correlation: Correlation,
        val payload: StatePayload
    )
}