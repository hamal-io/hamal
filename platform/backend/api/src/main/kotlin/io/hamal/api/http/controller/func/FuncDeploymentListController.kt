package io.hamal.api.http.controller.func

import io.hamal.core.adapter.func.FuncDeploymentListPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFuncDeploymentList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class FuncDeploymentListController(
    private val retry: Retry,
    private val funcDeploymentList: FuncDeploymentListPort,
) {
    @GetMapping("/v1/funcs/{funcId}/deployments")
    fun list(
        @PathVariable("funcId") funcId: FuncId
    ): ResponseEntity<ApiFuncDeploymentList> {
        return retry {
            funcDeploymentList.funcDeploymentList(funcId).let { li ->
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
}