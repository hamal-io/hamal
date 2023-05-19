package io.hamal.lib.sdk

import io.hamal.lib.sdk.service.DefaultExecService
import io.hamal.lib.sdk.service.ExecService

interface HamalSdk {
    fun execService(): ExecService
}

object DefaultHamalSdk : HamalSdk {
    override fun execService(): ExecService {
        return DefaultExecService()
    }

}