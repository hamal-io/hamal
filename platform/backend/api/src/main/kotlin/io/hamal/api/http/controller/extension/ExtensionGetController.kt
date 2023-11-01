package io.hamal.api.http.controller.extension

import io.hamal.core.adapter.ExtensionGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.sdk.api.ApiExtension
import io.hamal.lib.sdk.api.ApiExtension.*
import io.hamal.repository.api.Code
import io.hamal.repository.api.Extension
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class ExtensionGetController(
    private val retry: Retry,
    private val getExtension: ExtensionGetPort
) {
    @GetMapping("/v1/extensions/{extId}")
    fun getExtension(@PathVariable("extId") extId: ExtensionId): ResponseEntity<ApiExtension> = retry {
        getExtension(extId, ::assemble)
    }

    private fun assemble(ext: Extension, code: Code) =
        ResponseEntity.ok(
            ApiExtension(
                id = ext.id,
                name = ext.name,
                code = Code(
                    id = code.id,
                    version = code.version,
                    value = code.value
                )
            )
        )
}