package io.hamal.lib.sdk.hub

import io.hamal.lib.http.HttpTemplate

interface HubMetricService {
    fun countCompleted() : Int
    fun countFailed() : Int
}

internal class DefaultMetricService(
    private val template: HttpTemplate
) : HubMetricService {

    override fun countCompleted(): Int {
        TODO("Not yet implemented")
    }

    override fun countFailed(): Int {
        TODO("Not yet implemented")
    }
}