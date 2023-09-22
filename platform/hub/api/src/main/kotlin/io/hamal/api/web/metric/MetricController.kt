package io.hamal.api.web.metric

import io.hamal.core.service.MetricService
import io.hamal.repository.api.MetricData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    private val metricService: MetricService
) {
    @GetMapping("/v1/metrics/json")
    fun json(): ResponseEntity<MetricData> {
        return ResponseEntity.ok(
            metricService.query()
        )
    }
}