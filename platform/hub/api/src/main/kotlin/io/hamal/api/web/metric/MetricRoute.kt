package io.hamal.api.web.metric

import io.hamal.core.service.MetricService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MetricRoute(
    val metricService: MetricService
) {
    @GetMapping("/metric/json")
    fun metric(): ResponseEntity<String> {
        TODO() //    return ResponseEntity.ok(_,_)
    }
}