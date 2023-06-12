package io.hamal.lib.sdk

import io.hamal.lib.sdk.service.AdhocService
import io.hamal.lib.sdk.service.DefaultAdhocService
import io.hamal.lib.sdk.service.DefaultExecService
import io.hamal.lib.sdk.service.ExecService

interface HamalSdk {
    fun adhocService(): AdhocService
    fun execService(): ExecService
}

object DefaultHamalSdk : HamalSdk {
    override fun adhocService(): AdhocService {
        return DefaultAdhocService()
    }

    override fun execService(): ExecService {
        return DefaultExecService()
    }

}