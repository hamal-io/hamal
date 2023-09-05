package io.hamal.api.web.func

import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubFuncList
import io.hamal.lib.sdk.hub.HubFuncList.Func.Namespace
import io.hamal.repository.api.FuncQueryRepository
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/groups/{groupId}/funcs")
    fun listFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubFuncList> {
        val result = funcQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }

        val namespaces = namespaceQueryRepository.list(result.map { it.namespaceId }).associateBy { it.id }

        return ResponseEntity.ok(HubFuncList(
            result.map { func ->
                val namespace = namespaces[func.namespaceId]!!
                HubFuncList.Func(
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