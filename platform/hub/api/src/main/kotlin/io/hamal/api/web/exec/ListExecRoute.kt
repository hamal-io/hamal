package io.hamal.api.web.exec

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubExecList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListExecRoute(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository
) {
    @GetMapping("/v1/groups/{groupId}/execs")
    fun list(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubExecList> {
        val execs = execQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            HubExecList(
                execs = execs.map {
                    HubExecList.Exec(
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
