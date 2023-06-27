package io.hamal.backend.instance.service.cmd

import io.hamal.backend.instance.event.SystemEventEmitter
import io.hamal.backend.repository.api.StateCmdRepository
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.State
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StateCmdService
@Autowired constructor(
    val stateCmdRepository: StateCmdRepository,
    val eventEmitter: SystemEventEmitter<*>
) {

    fun set(cmdId: CmdId, stateToSet: StateToSet): State {
        TODO()
//        return stateCmdRepository.set(
//            cmdId, StateCmdRepository.StateToSet(
//                correlation = stateToSet.correlation,
//                state = stateToSet.payload,
//            )
//        )
        //FIXME emit event
    }

    data class StateToSet(
        val correlation: Correlation,
        val payload: State
    )
}