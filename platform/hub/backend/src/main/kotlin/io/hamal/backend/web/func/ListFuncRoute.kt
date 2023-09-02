package io.hamal.backend.web.func

import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiFuncList
import io.hamal.lib.sdk.domain.ApiFuncList.ApiSimpleFunc.Namespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiFuncList> {
        val result = funcQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }

        val namespaces = namespaceQueryRepository.list(result.map { it.namespaceId }).associateBy { it.id }

        return ResponseEntity.ok(ApiFuncList(
            result.map { func ->
                val namespace = namespaces[func.namespaceId]!!
                ApiFuncList.ApiSimpleFunc(
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