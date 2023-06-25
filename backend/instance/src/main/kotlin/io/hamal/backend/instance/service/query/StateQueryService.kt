package io.hamal.backend.instance.service.query

import io.hamal.backend.repository.api.StateQueryRepository
import io.hamal.lib.domain.CorrelatedState
import io.hamal.lib.domain.Correlation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StateQueryService
@Autowired constructor(
    private val stateQueryRepository: StateQueryRepository
) {

    fun find(correlation: Correlation) = stateQueryRepository.find(correlation)

    fun get(correlation: Correlation): CorrelatedState {
        return requireNotNull(stateQueryRepository.find(correlation)) { "No state found correlation $correlation" }
    }

}