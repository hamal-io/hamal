package io.hamal.api.http.endpoint.extension

import io.hamal.api.http.endpoint.req.Assembler.assemble
import io.hamal.core.adapter.GetExtensionPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sdk.api.ApiExtension
import io.hamal.lib.sdk.api.ApiExtension.*
import io.hamal.repository.api.Extension
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionGetController(
    private val retry: Retry,
    private val getExtension: GetExtensionPort
) {
    @GetMapping("/v1/extensions/{extId}")
    fun getExtension(@PathVariable("extId") extId: ExtensionId) = retry {
        getExtension(extId, ::assemble)
    }


    private fun assemble(ext: Extension) =
        ResponseEntity.ok(
            ApiExtension(
                id = ext.id,
                name = ext.name,
                code = ExtensionCode(
                    id = ext.code.id,
                    version = ext.code.version
                )
            )
        )
}