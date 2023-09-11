package io.hamal.admin.web.exec

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminExecList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListExecRoute(
    private val execQueryRepository: io.hamal.repository.api.ExecQueryRepository
) {

    @GetMapping("/v1/execs")
    fun listExec(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminExecList> {
        val execs = execQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            AdminExecList(
                execs = execs.map {
                    AdminExecList.Exec(
                        id = it.id,
                        status = it.status,
                        correlation = it.correlation,
                        func = null
                    )
                }
            )
        )
    }

    @GetMapping("/v1/groups/{groupId}/execs")
    fun listGroupExec(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminExecList> {
        val execs = execQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(
            AdminExecList(
                execs = execs.map {
                    AdminExecList.Exec(
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
