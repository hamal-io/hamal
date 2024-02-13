package io.hamal.api.http.controller.blueprint

import io.hamal.core.adapter.BlueprintListPort
import io.hamal.core.component.Retry
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.GroupId
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BlueprintListController(
    private val retry: Retry,
    private val getBlueprint: BlueprintListPort
) {

    @GetMapping("/v1/blueprints")
    fun listBlueprints(
        @RequestParam(required = false, name = "after_id", defaultValue = "7FFFFFFFFFFFFFFF") afterId: ExtensionId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit,
        @RequestParam(required = false, name = "group_ids", defaultValue = "") groupIds: List<GroupId>
    ) {
    }/*: ResponseEntity<ApiBlueprintList> {
        return ResponseEntity.ok(ApiBlueprintList(
            id = RequestId(value = SnowflakeId(value = 5986)),
            status = RequestStatus.Submitted
            //TODO(196)
        ))
    }*/

}