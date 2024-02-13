package io.hamal.api.http.controller.blueprint

import io.hamal.core.adapter.BlueprintListPort
import io.hamal.core.component.Retry
import io.hamal.lib.domain.vo.GroupId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintListController(
    private val retry: Retry,
    private val getBlueprint: BlueprintListPort
) {

    @GetMapping("/v1/groups/{groupId}/blueprints")
    fun listBlueprints(
        @PathVariable("groupId") groupId: GroupId
    ) {
    }/*: ResponseEntity<ApiBlueprintList> {
        return ResponseEntity.ok(ApiBlueprintList(
            id = RequestId(value = SnowflakeId(value = 5986)),
            status = RequestStatus.Submitted
            //TODO(196)
        ))
    }*/

}