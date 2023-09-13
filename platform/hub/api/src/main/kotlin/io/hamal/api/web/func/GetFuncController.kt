package io.hamal.api.web.func

import io.hamal.core.component.func.GetFunc
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubFunc
import io.hamal.repository.api.Func
import io.hamal.repository.api.Namespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetFuncController(private val getFunc: GetFunc) {

    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(@PathVariable("funcId") funcId: FuncId) = getFunc(funcId, ::assemble)

    private fun assemble(func: Func, namespace: Namespace) =
        ResponseEntity.ok(
            HubFunc(
                id = func.id,
                namespace = HubFunc.Namespace(
                    id = namespace.id,
                    name = namespace.name
                ),
                name = func.name,
                inputs = func.inputs,
                code = func.code
            )
        )
}