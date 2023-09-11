package io.hamal.api.web.metric

import io.hamal.core.service.MetricService
import kotlinx.serialization.Serializable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Serializable
data class MetricDataType(
    val time: Long,
    val map: LinkedHashMap<String, Int>
)

@RestController
class MetricRoute(
    val metricService: MetricService
) {
    @GetMapping("/v1/metrics/json", produces = ["application/json"])
    fun json(): ResponseEntity<MetricDataType> {
        val result = metricService.get()
        return ResponseEntity.ok(
            MetricDataType(result.getTime(), result.getMap())
        )
    }
}