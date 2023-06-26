package io.hamal.backend.instance.web.func

import io.hamal.backend.instance.service.query.FuncQueryService
import io.hamal.lib.domain.vo.*
import io.hamal.lib.sdk.domain.ListFuncsResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ListFuncRoute(
    private val queryService: FuncQueryService,
) {
    @GetMapping("/v1/funcs")
    fun listFunc(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: FuncId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListFuncsResponse> {
        val result = queryService.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(ListFuncsResponse(
            result.map {
                ListFuncsResponse.Func(
                    id = it.id,
                    name = it.name
                )
            }
        ))
    }
}