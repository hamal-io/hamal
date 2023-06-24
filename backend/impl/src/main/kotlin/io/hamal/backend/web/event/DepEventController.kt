package io.hamal.backend.web.event

import io.hamal.backend.event.Event
import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class DepEventController(
    @Autowired private val cmdService: EventCmdService<*>,
    @Autowired private val queryService: EventQueryService<*>
) {

    @PostMapping("/v1/topics/{topicId}/events")
    fun appendEvent(
        @PathVariable("topicId") topicId: String,
        @RequestHeader("Content-Type") contentType: String,
        @RequestBody body: ByteArray
    ): ResponseEntity<ApiAppendEventResponse> {

        cmdService.append(
            CmdId(TimeUtils.now().toEpochMilli()), // FIXME
            EventCmdService.EventToAppend(
                cmdId = CmdId(1),
                topicId = TopicId(topicId.toInt()),
                contentTpe = contentType,
                value = body
            )
        )

        return ResponseEntity.ok(
            ApiAppendEventResponse(
                topicId = TopicId(1),
                topicName = TopicName("TBD")
            )
        )
    }

    @GetMapping("/v1/topics/{topicId}/events")
    fun listEvents(
        @PathVariable("topicId") topicId: String,
        @RequestParam(required = false, name = "stringEvtId", defaultValue = "0") stringEvtId: String,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Int
    ): ResponseEntity<ApiListEventResponse> {
        return ResponseEntity.ok(
            ApiListEventResponse(
                topicId = TopicId(0),
                topicName = TopicName(""),
                events = queryService.queryEvents(
                    EventQueryService.EventQuery(
                        topicId = TopicId(topicId.toInt())
                    )
                ).map {
                    require(it is Event)
                    ApiListEventResponse.Event(
                        contentType = it.contentType,
                        value = String(it.value)
                    )
                }
            )
        )
    }

}