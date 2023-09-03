package io.hamal.backend.web.exec

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.hub.domain.ApiExecList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListExecRoute(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository
) {
    @GetMapping("/v1/execs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiExecList> {
        val execs = execQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ApiExecList(
                execs = execs.map {
                    ApiExecList.SimpleExec(
                        id = it.id,
                        status = it.status,
                        correlation = it.correlation,
                        func = null
                    )
                }
            )
        )
    }
}
