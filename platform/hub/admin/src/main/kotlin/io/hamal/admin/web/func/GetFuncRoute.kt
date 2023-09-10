package io.hamal.admin.web.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.admin.AdminFunc
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class GetFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(
        @PathVariable("funcId") funcId: FuncId,
    ): ResponseEntity<AdminFunc> {
        val result = funcQueryRepository.get(funcId)
        val namespace = namespaceQueryRepository.get(result.namespaceId)
        return ResponseEntity.ok(result.let {
            AdminFunc(
                id = it.id,
                namespace = AdminFunc.Namespace(
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