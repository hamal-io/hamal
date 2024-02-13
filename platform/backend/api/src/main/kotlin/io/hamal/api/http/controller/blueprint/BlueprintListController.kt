package io.hamal.api.http.controller.blueprint

import io.hamal.core.adapter.BlueprintListPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.BlueprintDescription
import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.sdk.api.ApiBlueprintList
import io.hamal.lib.sdk.api.ApiBlueprintList.Blueprint
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintListController(
    private val retry: Retry,
    private val getBlueprints: BlueprintListPort
) {

    @GetMapping("/v1/groups/{groupId}/blueprints")
    fun listBlueprints(
        @PathVariable("groupId") groupId: GroupId
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


