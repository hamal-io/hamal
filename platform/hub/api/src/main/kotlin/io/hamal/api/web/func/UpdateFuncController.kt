package io.hamal.api.web.func

import io.hamal.api.web.req.Assembler
import io.hamal.core.component.func.UpdateFunc
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.sdk.hub.HubUpdateFuncReq
import org.springframework.http.HttpStatus.ACCEPTED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class UpdateFuncController(private val updateFunc: UpdateFunc) {
    @PutMapping("/v1/funcs/{funcId}")
    fun createFunc(@PathVariable("funcId") funcId: FuncId, @RequestBody req: HubUpdateFuncReq) =
        updateFunc(funcId, req) { submittedReq ->
            ResponseEntity(Assembler.assemble(submittedReq), ACCEPTED)
        }
}