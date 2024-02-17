package io.hamal.api.http.controller.blueprint

import io.hamal.core.adapter.BlueprintGetPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sdk.api.ApiBlueprint
import io.hamal.repository.api.Blueprint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
internal class BlueprintGetController(
    private val retry: Retry,
    private val getBlueprint: BlueprintGetPort
) {
    @GetMapping("/v1/blueprints/{bpId}")
    fun getBlueprint(@PathVariable("bpId") blueprintId: BlueprintId) = retry {
        getBlueprint(blueprintId, ::assemble)
    }

    private fun assemble(blueprint: Blueprint) =
        ResponseEntity.ok(
            ApiBlueprint(
                id = blueprint.id,
                name = blueprint.name,
                inputs = blueprint.inputs,
                value = blueprint.value,
                description = blueprint.description
            )
        )
}