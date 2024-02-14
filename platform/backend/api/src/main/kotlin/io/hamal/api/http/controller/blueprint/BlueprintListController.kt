package io.hamal.api.http.controller.blueprint

import io.hamal.lib.domain.vo.BlueprintDescription
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.sdk.api.ApiBlueprintList
import io.hamal.lib.sdk.api.ApiBlueprintList.Blueprint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintListController(
    //private val getBlueprints: BlueprintListPort
) {

    @GetMapping("/v1/blueprints")
    fun listBlueprints(
    ): ResponseEntity<ApiBlueprintList> {
        return ResponseEntity.ok(
            ApiBlueprintList(
                listOf(
                    Blueprint(
                        id = BlueprintId(12343),
                        name = BlueprintName("BlueprintBotName"),
                        description = BlueprintDescription("Nice Blueprint")
                    ),
                    Blueprint(
                        id = BlueprintId(5311),
                        name = BlueprintName("BlueprintBotName2"),
                        description = BlueprintDescription("Nicer Blueprint")
                    )
                )
            )
        )

    }
}


