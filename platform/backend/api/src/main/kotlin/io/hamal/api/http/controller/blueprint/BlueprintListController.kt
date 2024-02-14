package io.hamal.api.http.controller.blueprint

import io.hamal.core.adapter.BlueprintListPort
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.sdk.api.ApiBlueprintList
import io.hamal.lib.sdk.api.ApiBlueprintList.Blueprint
import io.hamal.repository.api.BlueprintQueryRepository.BlueprintQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintListController(
    private val listBlueprints: BlueprintListPort
) {

    @GetMapping("/v1/blueprints")
    fun listBlueprints(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") bpId: BlueprintId,
        @RequestParam(required = false, name = "limit", defaultValue = "10") limit: Limit,
        @RequestParam(required = false, name = "blueprint_ids", defaultValue = "") bpIds: List<BlueprintId>,
    ): ResponseEntity<ApiBlueprintList> {
        return listBlueprints(
            BlueprintQuery(
                afterId = bpId,
                limit = limit,
                blueprintIds = bpIds
            )
        ) { blueprints ->
            ResponseEntity.ok(
                ApiBlueprintList(
                    blueprints.map { blueprint ->
                        Blueprint(
                            id = blueprint.id,
                            name = blueprint.name,
                            description = blueprint.description
                        )
                    }
                )
            )

        }
    }
}


