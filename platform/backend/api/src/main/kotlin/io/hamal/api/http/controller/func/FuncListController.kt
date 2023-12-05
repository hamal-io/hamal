package io.hamal.api.http.controller.func

import io.hamal.core.adapter.FuncListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiFuncDeploymentList
import io.hamal.lib.sdk.api.ApiFuncList
import io.hamal.lib.sdk.api.ApiFuncList.Func
import io.hamal.lib.sdk.api.ApiFuncList.Func.Flow
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncListController(private val listFunc: FuncListPort) {

    @GetMapping("/v1/flows/{flowId}/funcs")
    fun listFlowFuncs(
        @PathVariable("flowId") flowId: FlowId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "10") limit: Limit,
    ): ResponseEntity<ApiFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit,
                flowIds = listOf(flowId)
            ),
            // assembler
        ) { funcs, flows ->

            ResponseEntity.ok(ApiFuncList(
                funcs.map { func ->
                    val flow = flows[func.flowId]!!
                    Func(
                        id = func.id,
                        flow = Flow(
                            id = flow.id,
                            name = flow.name
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
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>,
        @RequestParam(required = false, name = "flow_ids", defaultValue = "") flowIds: List<FlowId>
    ): ResponseEntity<ApiFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit,
                groupIds = groupIds,
                flowIds = flowIds
            ),
            // assembler
        ) { funcs, flows ->

            ResponseEntity.ok(ApiFuncList(
                funcs.map { func ->
                    val flow = flows[func.flowId]!!
                    Func(
                        id = func.id,
                        flow = Flow(
                            id = flow.id,
                            name = flow.name
                        ),
                        name = func.name
                    )
                }
            ))

        }
    }

    @GetMapping("/v1/funcs/{funcId}/deployments")
    fun listFuncDeployments(
        @PathVariable("funcId") funcId: FuncId,
        @RequestParam(required = false, name = "limit") limit: Limit
    ): ResponseEntity<ApiFuncDeploymentList> {
        return listFunc(funcId) { li ->
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