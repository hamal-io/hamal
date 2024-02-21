package io.hamal.api.http.controller.blueprint

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.BlueprintCreatePort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintCreateController(
    private val retry: Retry,
    private val createBlueprint: BlueprintCreatePort
) {
    @PostMapping("/v1/blueprints")
    fun createBlueprint(
        @RequestBody req: ApiBlueprintCreateRequest
    ): ResponseEntity<ApiRequested> = retry {
        // 227 - FIXME remove hardcode account id
        createBlueprint(AccountId(1), req).accepted()
    }
}