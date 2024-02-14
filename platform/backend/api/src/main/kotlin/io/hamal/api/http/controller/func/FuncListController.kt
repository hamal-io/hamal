package io.hamal.api.http.controller.func

import io.hamal.core.adapter.FuncDeploymentListPort
import io.hamal.core.adapter.FuncListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.sdk.api.ApiFuncDeploymentList
import io.hamal.lib.sdk.api.ApiFuncList
import io.hamal.lib.sdk.api.ApiFuncList.Func
import io.hamal.lib.sdk.api.ApiFuncList.Func.Namespace
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncListController(
    private val listFunc: FuncListPort,
    private val listDeployments: FuncDeploymentListPort
) {

    @GetMapping("/v1/namespaces/{namespaceId}/funcs")
    fun listNamespaceFuncs(
        @PathVariable("namespaceId") namespaceId: NamespaceId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "10") limit: Limit,
    ): ResponseEntity<ApiFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit,
                namespaceIds = listOf(namespaceId)
            ),
            // assembler
        ) { funcs, namespaces ->

            ResponseEntity.ok(ApiFuncList(
                funcs.map { func ->
                    val namespace = namespaces[func.namespaceId]!!
                    Func(
                        id = func.id,
                        namespace = Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        name = func.name
                    )
                }
            ))

        }
    }

    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "workspace_ids", defaultValue = "") workspaceIds: List<WorkspaceId>,
        @RequestParam(required = false, name = "namespace_ids", defaultValue = "") namespaceIds: List<NamespaceId>
    ): ResponseEntity<ApiFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit,
                workspaceIds = workspaceIds,
                namespaceIds = namespaceIds
            ),
            // assembler
        ) { funcs, namespaces ->

            ResponseEntity.ok(ApiFuncList(
                funcs.map { func ->
                    val namespace = namespaces[func.namespaceId]!!
                    Func(
                        id = func.id,
                        namespace = Namespace(
                            id = namespace.id,
                            name = namespace.name
                        ),
                        name = func.name
                    )
                }
            ))

        }
    }

    @GetMapping("/v1/funcs/{funcId}/deployments")
    fun listFuncDeployments(
        @PathVariable("funcId") funcId: FuncId
    ): ResponseEntity<ApiFuncDeploymentList> {
        return listDeployments(funcId) { li ->
            ResponseEntity.ok(ApiFuncDeploymentList(
                deployments = li.map { res ->
                    ApiFuncDeploymentList.Deployment(
                        res.version, res.message, res.deployedAt
                    )
                }
            ))
        }
    }
}