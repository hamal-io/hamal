package com.nyanbot.http.controller.adhoc

import com.nyanbot.component.Hamal
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.InvocationInputs
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class AdhocInvokeRequest(
    val inputs: InvocationInputs? = null,
    val code: CodeValue
)


@RestController
internal class AdhocController(
    private val hamal: Hamal
) {

    @PostMapping("/v1/adhoc")
    fun invoke(
        @RequestBody req: AdhocInvokeRequest
    ) {
        hamal.setup()

    }

}