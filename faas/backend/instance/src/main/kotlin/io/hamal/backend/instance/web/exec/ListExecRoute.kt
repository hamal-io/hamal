package io.hamal.backend.instance.web.exec

import io.hamal.backend.repository.api.ExecQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.domain.ListExecsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListExecRoute(
    private val execQueryRepository: ExecQueryRepository
) {
    @GetMapping("/v1/execs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "${Long.MAX_VALUE}") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListExecsResponse> {
        val execs = execQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            ListExecsResponse(
                execs = execs.map {
                    ListExecsResponse.Exec(
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