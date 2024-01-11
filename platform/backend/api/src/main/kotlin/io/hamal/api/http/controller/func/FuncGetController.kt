package io.hamal.api.http.controller.func

import io.hamal.core.adapter.FuncGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiFunc.*
import io.hamal.repository.api.Code
import io.hamal.repository.api.Flow
import io.hamal.repository.api.Func
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncGetController(
    private val retry: Retry,
    private val getFunc: FuncGetPort
) {

    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(@PathVariable("funcId") funcId: FuncId): ResponseEntity<ApiFunc> = retry {
        getFunc(funcId, ::assemble)
    }


    private fun assemble(func: Func, current: Code, deployed: Code, flow: Flow) =
        ResponseEntity.ok(
            ApiFunc(
                id = func.id,
                flow = Flow(
                    id = flow.id,
                    name = flow.name
                ),
                name = func.name,
                inputs = func.inputs,
                code = Code(
                    id = func.code.id,
                    version = current.version,
                    value = current.value,
                ),
                deployment = Deployment(
                    id = func.deployment.id,
                    version = deployed.version,
                    value = deployed.value,
                    message = func.deployment.message
                )
            )
        )
}