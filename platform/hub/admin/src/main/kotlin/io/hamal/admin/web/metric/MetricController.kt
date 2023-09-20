package io.hamal.admin.web.metric

import io.hamal.lib.sdk.admin.MetricData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    //private val metricService: MetricService
) {
    @GetMapping("/v1/metrics/json", produces = ["application/json"])
    fun json(): ResponseEntity<MetricData> {
        return ResponseEntity.ok(
            MetricData(
                123,
                mutableListOf(MetricData.Count("t1", 5))
            )
        )
    }

    @GetMapping("/v1/metrics/test", produces = ["application/json"])
    fun test(): ResponseEntity<String> {
        return ResponseEntity.ok("Test")

    }


}