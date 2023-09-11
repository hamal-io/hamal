package io.hamal.api.web.metric

import io.hamal.core.service.MetricService
import io.hamal.lib.sdk.hub.MetricDataType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController



@RestController
class MetricRoute(
    val metricService: MetricService
) {
    @GetMapping("/v1/metrics/json")
    fun asJSON(): ResponseEntity<String> {
        TODO() //    return ResponseEntity.ok(_,_)
    }


    @GetMapping("/v1/metrics/test")
    fun test(): ResponseEntity<MetricDataType>{
        val result = metricService.get()
        return ResponseEntity.ok(
            MetricDataType(result.getTime(), result.getMap())
        )
    }
}