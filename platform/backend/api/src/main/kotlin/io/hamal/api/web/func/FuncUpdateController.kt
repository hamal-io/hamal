package io.hamal.api.web.func

import io.hamal.api.web.req.Assembler
import io.hamal.core.adapter.UpdateFuncPort
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.api.ApiUpdateFuncReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class FuncUpdateController(private val updateFunc: UpdateFuncPort) {
    @PutMapping("/v1/funcs/{funcId}")
    fun createFunc(@PathVariable("funcId") funcId: FuncId, @RequestBody req: ApiUpdateFuncReq) =
        updateFunc(funcId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
}