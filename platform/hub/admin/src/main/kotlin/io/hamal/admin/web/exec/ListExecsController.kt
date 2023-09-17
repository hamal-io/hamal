package io.hamal.admin.web.exec

import io.hamal.core.adapter.ListExecsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.admin.AdminExecList
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListExecsController(private val listExec: ListExecsPort) {
    @GetMapping("/v1/groups/{groupId}/execs")
    fun list(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<AdminExecList> {
        return listExec(
            ExecQueryRepository.ExecQuery(
                afterId = afterId,
                limit = limit
                // group id
            )
        ) { execs ->
            ResponseEntity.ok(
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
}
