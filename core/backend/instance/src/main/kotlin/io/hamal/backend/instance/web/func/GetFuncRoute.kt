package io.hamal.backend.instance.web.func

import io.hamal.backend.repository.api.FuncQueryRepository
import io.hamal.lib.domain.Func
import io.hamal.lib.domain.vo.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class GetFuncRoute(
    private val funcQueryRepository: FuncQueryRepository,
) {
    @GetMapping("/v1/funcs/{funcId}")
    fun getFunc(
        @PathVariable("funcId") funcId: FuncId,
    ): ResponseEntity<Func> {
        val result = funcQueryRepository.get(funcId)
        return ResponseEntity.ok(result)
    }
}