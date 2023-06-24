package io.hamal.backend.web.event

import io.hamal.backend.service.query.EventQueryService
import io.hamal.lib.sdk.domain.*
import io.hamal.lib.sdk.domain.ListTopicsResponse.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
open class ListTopicsController(
    @Autowired private val queryService: EventQueryService<*>
) {

    @GetMapping("/v1/topics")
    fun listTopics(): ResponseEntity<ListTopicsResponse> {
        return ResponseEntity.ok(
            ListTopicsResponse(
                topics = queryService.queryTopics {

                }.map { topic ->
                    Topic(
                        id = topic.id,
                        name = topic.name
                    )
                }
            )
        )
    }

}