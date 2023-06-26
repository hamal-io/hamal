package io.hamal.backend.instance.web.trigger

import io.hamal.backend.instance.service.cmd.TriggerCmdService
import io.hamal.backend.instance.service.query.TriggerQueryService
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain._enum.TriggerType
import io.hamal.lib.domain.vo.Limit
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.sdk.domain.ListTriggersResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.*


@RestController
open class TriggerController(
    @Autowired val queryService: TriggerQueryService,
    @Autowired val cmdService: TriggerCmdService
) {
    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringTriggerId: String,
        @RequestParam(required = false, name = "types", defaultValue = "") typesString: List<String>,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ListTriggersResponse> {

        val types = typesString.map { TriggerType.valueOf(it) }

        val result =
            queryService.list {
                this.afterId = TriggerId(SnowflakeId(stringTriggerId.toLong()))
                this.types = types.toSet()
                this.limit = limit
            }

        return ResponseEntity.ok(
            ListTriggersResponse(
                result.map {
                    ListTriggersResponse.Trigger(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
    }
}
