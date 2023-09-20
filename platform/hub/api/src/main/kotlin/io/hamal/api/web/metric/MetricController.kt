package io.hamal.admin.web.metric

import io.hamal.core.service.MetricService
import io.hamal.repository.api.MetricData
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    private val metricService: MetricService
) {

    @GetMapping("/v1/metrics/json", produces = ["application/json"])
    fun json(): ResponseEntity<MetricData> {
        return ResponseEntity.ok(metricService.query())
    }


    @PostMapping("/v1/metrics/clear")
    fun clear() {
        metricService.clear()
    }

    /*    private fun toJson(s: MetricAccess): JsonObject {
            val arr = buildJsonArray {
                for (i in s.getMap()) {
                    addJsonObject {
                        put(i.key, i.value)
                    }
                }
            }

            return buildJsonObject {
                put("timer", s.getTime())
                put("events", arr)
            }
        }*/

}