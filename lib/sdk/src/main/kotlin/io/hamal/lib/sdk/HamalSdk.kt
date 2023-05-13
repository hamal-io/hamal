package io.hamal.lib.sdk

import io.hamal.lib.sdk.service.DefaultJobService
import io.hamal.lib.sdk.service.JobService

interface HamalSdk {
    fun jobService(): JobService
}

object DefaultHamalSdk : HamalSdk {
    override fun jobService(): JobService {
        return DefaultJobService()
    }

}