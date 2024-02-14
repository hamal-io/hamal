package io.hamal.api.http.controller.exec

import io.hamal.core.adapter.ExecListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.api.ApiExecList
import io.hamal.repository.api.ExecQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExecListController(private val listExec: ExecListPort) {

    @GetMapping("/v1/namespaces/{namespaceId}/execs")
    fun namespaceExecList(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
    ): ResponseEntity<ApiExecList> {
        return listExec(
            ExecQueryRepository.ExecQuery(
                afterId = afterId,
                limit = limit,
                namespaceIds = listOf(namespaceId)
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

    @GetMapping("/v1/execs")
    fun list(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExecId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "func_ids", defaultValue = "") funcIds: List<FuncId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiExecList> {
        return listExec(
            ExecQueryRepository.ExecQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = workspaceIds,
                funcIds = funcIds,
                namespaceIds = namespaceIds
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
