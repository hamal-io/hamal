package io.hamal.backend.instance.web.func

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiFuncList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
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
        return ResponseEntity.ok(ApiFuncList(
            result.map {
                ApiFuncList.ApiSimpleFunc(
                    id = it.id,
                    name = it.name
                )
            }
        ))
    }
}