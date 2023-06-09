package io.hamal.backend.web

import io.hamal.backend.repository.api.domain.Tenant
import io.hamal.backend.service.cmd.TriggerCmdService
import io.hamal.backend.service.cmd.TriggerCmdService.TriggerToCreate
import io.hamal.backend.service.query.TriggerQueryService
import io.hamal.lib.common.Shard
import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TriggerId
import io.hamal.lib.domain.vo.TriggerInputs
import io.hamal.lib.domain.vo.TriggerSecrets
import io.hamal.lib.script.api.value.TableValue
import io.hamal.lib.sdk.domain.ApiCreateTriggerRequest
import io.hamal.lib.sdk.domain.ApiCreateTriggerResponse
import io.hamal.lib.sdk.domain.ApiListTriggerResponse
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.time.*


@RestController
open class TriggerController(
    @Autowired val queryService: TriggerQueryService,
    @Autowired val cmdService: TriggerCmdService
) {
    @PostMapping("/v1/triggers")
    fun createTrigger(
        @RequestAttribute shard: Shard,
        @RequestAttribute tenant: Tenant,
        @RequestBody req: ApiCreateTriggerRequest
    ): ResponseEntity<ApiCreateTriggerResponse> {


        //FIXME replace with request
        val result = cmdService.create(
            CmdId(0), TriggerToCreate(
                shard = shard,
                name = req.name,
                funcId = req.funcId,
                type = req.type,
                inputs = TriggerInputs(TableValue()),
                secrets = TriggerSecrets(listOf()),
                duration = req.duration
            )
        )

        return ResponseEntity(
            ApiCreateTriggerResponse(
                id = result.id,
                name = result.name,
            ),
            HttpStatus.CREATED
        )
    }


    @GetMapping("/v1/triggers")
    fun listTrigger(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") stringTriggerId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListTriggerResponse> {
        val result =
            queryService.list(
                afterId = TriggerId(SnowflakeId(stringTriggerId.toLong())),
                limit = limit
            )


        return ResponseEntity.ok(
            ApiListTriggerResponse(
                result.map {
                    ApiListTriggerResponse.Trigger(
                        id = it.id,
                        name = it.name
                    )
                }
            )
        )
    }
}
