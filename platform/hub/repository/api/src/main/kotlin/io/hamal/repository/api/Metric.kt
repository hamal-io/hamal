package io.hamal.repository.api

import io.hamal.repository.api.event.HubEvent
import org.jetbrains.annotations.TestOnly

interface MetricAccess {
    fun getTime(): Long
    fun getMap(): LinkedHashMap<String, Int>
}

interface MetricRepository {
    fun create()
    fun update(e: HubEvent, transform: (HubEvent) -> String)
    fun getData(): MetricAccess
    fun clear()
    fun setTimer(timer: Long)
}


