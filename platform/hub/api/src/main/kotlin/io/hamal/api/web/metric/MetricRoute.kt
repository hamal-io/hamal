package io.hamal.api.web.metric

import io.hamal.core.service.MetricService
import io.hamal.lib.sdk.hub.MetricType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricRoute(
    val metricService: MetricService
) {
    @GetMapping("/v1/metrics/json", produces = ["application/json"])
    fun json(): ResponseEntity<MetricType> {
        val result = metricService.get()
        return ResponseEntity.ok(
            MetricType(result.getTime(), result.getMap())
        )
    }
}