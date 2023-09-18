package io.hamal.admin.web.func

import io.hamal.core.adapter.ListFuncsPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.sdk.admin.AdminFuncList
import io.hamal.lib.sdk.admin.AdminFuncList.*
import io.hamal.lib.sdk.admin.AdminFuncList.Func.*
import io.hamal.repository.api.Func
import io.hamal.repository.api.FuncQueryRepository.FuncQuery
import io.hamal.repository.api.Namespace
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ListFuncsController(private val listFunc: ListFuncsPort) {
    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>
    ): ResponseEntity<AdminFuncList> {
        return listFunc(
            FuncQuery(
                afterId = afterId,
                limit = limit
                //groupid
            ),
            ::assemble
        )
    }

    private fun assemble(funcs: List<Func>, namespaces: Map<NamespaceId, Namespace>) =
        ResponseEntity.ok(AdminFuncList(
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