package io.hamal.api.http.endpoint.func

import io.hamal.core.adapter.FuncGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiFunc
import io.hamal.lib.sdk.api.ApiFunc.*
import io.hamal.repository.api.Code
import io.hamal.repository.api.Func
import io.hamal.repository.api.Namespace
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
    fun getFunc(@PathVariable("funcId") funcId: FuncId) = retry {
        getFunc(funcId, ::assemble)
    }

    private fun assemble(func: Func, code: Code, namespace: Namespace) =
        ResponseEntity.ok(
            ApiFunc(
                id = func.id,
                namespace = Namespace(
                    id = namespace.id,
                    name = namespace.name
                ),
                name = func.name,
                inputs = func.inputs,
                code = Code(
                    id = code.id,
                    version = code.version,
                    value = code.value
                )
            )
        )
}