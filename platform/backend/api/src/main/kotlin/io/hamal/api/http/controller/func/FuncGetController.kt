package io.hamal.api.http.controller.func

import io.hamal.core.adapter.code.CodeGetPort
import io.hamal.core.adapter.func.FuncGetPort
import io.hamal.core.adapter.namespace.NamespaceGetPort
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
    private val funcGet: FuncGetPort,
    private val codeGet: CodeGetPort,
    private val namespaceGet: NamespaceGetPort
) {

    @GetMapping("/v1/funcs/{funcId}")
    fun get(@PathVariable("funcId") funcId: FuncId): ResponseEntity<ApiFunc> = retry {
        funcGet(funcId).let { func ->
            assemble(
                func,
                codeGet(func.code.id, func.code.version),
                codeGet(func.deployment.id, func.deployment.version),
                namespaceGet(func.namespaceId)
            )
        }
    }

    private fun assemble(func: Func, current: Code, deployed: Code, namespace: Namespace) =
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