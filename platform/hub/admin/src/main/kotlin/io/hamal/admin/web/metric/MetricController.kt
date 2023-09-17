package io.hamal.admin.web.metric

import io.hamal.core.service.MetricService
import io.hamal.lib.sdk.admin.AdminMetrics
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    private val metricService: MetricService
) {
    @GetMapping("/v1/metrics", produces = ["application/json"])
    fun json(): ResponseEntity<AdminMetrics> {
        val result = metricService.get()
        return ResponseEntity.ok(
            AdminMetrics(result.getTime(), result.getMap())
        )
    }
}