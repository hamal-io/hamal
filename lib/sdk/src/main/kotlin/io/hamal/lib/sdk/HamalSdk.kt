package io.hamal.lib.sdk

import io.hamal.lib.sdk.service.DefaultQueueService
import io.hamal.lib.sdk.service.QueueService

interface HamalSdk {
    fun queueService(): QueueService
}

object DefaultHamalSdk : HamalSdk {
    override fun queueService(): QueueService {
        return DefaultQueueService()
    }

}