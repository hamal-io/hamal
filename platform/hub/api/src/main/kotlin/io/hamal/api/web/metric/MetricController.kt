package io.hamal.admin.web.metric

import io.hamal.core.service.MetricService
import io.hamal.lib.sdk.admin.AdminMetrics
import io.hamal.repository.api.MetricAccess
import kotlinx.serialization.json.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MetricController(
    private val metricService: MetricService
) {

    @GetMapping("/v1/metrics/json", produces = ["application/json"])
    fun json(): ResponseEntity<AdminMetrics> {
        val res = metricService.query()
        return ResponseEntity.ok(
            AdminMetrics(res.getTime(), )
        )
    }

    /*  @GetMapping("/v1/metrics/json", *//*produces = ["application/json"]*//*)
    fun json(): ResponseEntity<String> {
        val res = metricService.query()
        val str = toJson(res)

        return ResponseEntity.ok(
            str.toString()
        )
    }*/

    @PostMapping("/v1/metrics/clear")
    fun clear() {
        metricService.clear()
    }

    private fun <> mapToList(map: LinkedHashMap<>): List<AdminMetrics.Count> {

    }

    private fun toJson(s: MetricAccess): JsonObject {
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
    }

}