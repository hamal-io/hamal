package io.hamal.api.http.endpoint.exec

import io.hamal.core.adapter.ListExecsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiExecList
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecListController(private val listExec: ListExecsPort) {

    @GetMapping("/v1/execs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId>
    ): ResponseEntity<ApiExecList> {
        return listExec(
            ExecQueryRepository.ExecQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds,
                funcIds = funcIds
            )
        ) { execs ->
            ResponseEntity.ok(
                ApiExecList(
                    execs = execs.map {
                        ApiExecList.Exec(
                            id = it.id,
                            status = it.status,
                            correlation = it.correlation,
                        )
                    }
                )
            )
        }
    }
}
