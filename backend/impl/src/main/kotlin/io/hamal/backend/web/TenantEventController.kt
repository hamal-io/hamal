package io.hamal.backend.web

import io.hamal.backend.event.TenantEvent
import io.hamal.backend.req.SubmitRequest
import io.hamal.backend.service.cmd.EventCmdService
import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.common.Partition
import io.hamal.lib.common.util.TimeUtils
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.req.CreateTopicReq
import io.hamal.lib.domain.req.SubmittedCreateTopicReq
import io.hamal.lib.domain.vo.TenantId
import io.hamal.lib.domain.vo.TopicId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.sdk.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class TenantEventController(
    @Autowired private val submitRequest: SubmitRequest,
    @Autowired private val cmdService: EventCmdService<*>,
    @Autowired private val queryService: EventQueryService<*>
) {

    @GetMapping("/v1/topics")
    fun listTopics(): ResponseEntity<ApiListTopicResponse> {
        return ResponseEntity.ok(
            ApiListTopicResponse(
                topics = queryService.queryTopics {

                }.map { topic ->
                    ApiListTopicResponse.Topic(
                        id = topic.id,
                        name = topic.name
                    )
                }
            )
        )
    }

    @PostMapping("/v1/topics")
    fun createTopic(
        @RequestBody createTopic: CreateTopicReq
    ): ResponseEntity<SubmittedCreateTopicReq> {
        val result = submitRequest(createTopic)
        return ResponseEntity(result, HttpStatus.ACCEPTED)
    }

    //    @PostMapping("/v1/topics")
//    fun depcreateTopic(
//        @RequestBody request: ApiCreateTopicRequest
//    ): ResponseEntity<ApiCreateTopicResponse> {
//
//        return ResponseEntity.ok(
//            with(
//                cmdService.create(
//                    EventCmdService.TopicToCreate(
//                        cmdId = CmdId(1),
//                        partition = Partition(1),
//                        tenantId = TenantId(1),
//                        name = request.name
//                    )
//                )
//            ) {
//                ApiCreateTopicResponse(
//                    id = this.id,
//                    name = this.name
//                )
//            }
//        )
//    }


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
                partition = Partition(1),
                tenantId = TenantId(1),
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
                    require(it is TenantEvent)
                    ApiListEventResponse.Event(
                        contentType = it.contentType,
                        value = String(it.value)
                    )
                }
            )
        )
    }

}