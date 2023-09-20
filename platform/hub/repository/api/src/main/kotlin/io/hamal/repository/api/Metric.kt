package io.hamal.repository.api

import io.hamal.repository.api.event.HubEvent
import kotlinx.serialization.Serializable


@Serializable
data class MetricData(
    val time: Long = System.currentTimeMillis(),
    val events: MutableList<Count> = mutableListOf()
) {
    @Serializable
    data class Count(
        val name: String,
        var value: Int
    )
}

interface MetricRepository {
    fun update(e: HubEvent, transform: (HubEvent) -> String)
    fun get(): MetricData
    fun clear()
}


