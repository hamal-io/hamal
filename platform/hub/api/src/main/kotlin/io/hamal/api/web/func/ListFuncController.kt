package io.hamal.api.web.func

import io.hamal.core.component.func.ListFunc
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.hub.HubFuncList
import io.hamal.lib.sdk.hub.HubFuncList.*
import io.hamal.lib.sdk.hub.HubFuncList.Func.*
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.NamespaceQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListFuncController(
    private val listFunc: ListFunc,
    private val namespaceQueryRepository: NamespaceQueryRepository
) {
    @GetMapping("/v1/groups/{groupId}/funcs")
    fun listFunc(
        @PathVariable("groupId") groupId: GroupId,
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<HubFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit
                // groupId = ...
            ),
            // assembler
        ) { funcs, namespaces ->

            ResponseEntity.ok(HubFuncList(
                funcs.map { func ->
                    val namespace = namespaces[func.namespaceId]!!
                    Func(
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
}