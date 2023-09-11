package io.hamal.lib.sdk.hub

import io.hamal.lib.http.HttpTemplate

interface HubMetricService {
    fun get()
}

internal class DefaultMetricService(
    private val template: HttpTemplate
) : HubMetricService {

    override fun get() {
     TODO()
    }
}