package io.hamal.admin.web.state

import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain.vo.CorrelationId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminCorrelatedState
import io.hamal.lib.sdk.admin.AdminCorrelation
import io.hamal.lib.sdk.admin.AdminState
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.StateQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetStateRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val stateQueryRepository: StateQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}/states/{correlationId}")
    fun getState(
        @PathVariable("funcId") funcId: FuncId,
        @PathVariable("correlationId") correlationId: CorrelationId,
    ): ResponseEntity<AdminCorrelatedState> {
        val func = funcQueryRepository.get(funcId)
        val result = stateQueryRepository.get(Correlation(correlationId, funcId))
        return ResponseEntity.ok(
            AdminCorrelatedState(
                correlation = AdminCorrelation(
                    correlationId = correlationId,
                    func = AdminCorrelation.Func(
                        id = func.id,
                        name = func.name
                    )
                ),
                state = AdminState(result.value.value)
            )
        )
    }
}