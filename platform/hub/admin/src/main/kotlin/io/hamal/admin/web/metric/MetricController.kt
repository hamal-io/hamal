package io.hamal.admin.web.metric

import io.hamal.core.service.MetricService
import io.hamal.lib.sdk.admin.AdminMetrics
import io.hamal.repository.api.MetricAccess
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    private val metricService: MetricService
) {

    /*
        @GetMapping("/v1/metrics", produces = ["application/json"])
        fun json(): ResponseEntity<AdminMetrics> {
            return ResponseEntity.ok()
        }
    */
}