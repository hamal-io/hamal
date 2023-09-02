package io.hamal.backend.web.func

import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiFunc
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(
        @PathVariable("funcId") funcId: FuncId,
    ): ResponseEntity<ApiFunc> {
        val result = funcQueryRepository.get(funcId)
        val namespace = namespaceQueryRepository.get(result.namespaceId)
        return ResponseEntity.ok(result.let {
            ApiFunc(
                id = it.id,
                namespace = ApiFunc.Namespace(
                    id = namespace.id,
                    name = namespace.name
                ),
                name = it.name,
                inputs = it.inputs,
                code = it.code
            )
        })
    }
}