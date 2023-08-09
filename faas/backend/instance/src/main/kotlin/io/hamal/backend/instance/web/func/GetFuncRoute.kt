package io.hamal.backend.instance.web.func

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.domain.ApiFunc
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class GetFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
) {
    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(
        @PathVariable("funcId") funcId: FuncId,
    ): ResponseEntity<ApiFunc> {
        val result = funcQueryRepository.get(funcId)
        return ResponseEntity.ok(result.let {
            ApiFunc(
                id = it.id,
                name = it.name,
                inputs = it.inputs,
                code = it.code
            )
        })
    }
}