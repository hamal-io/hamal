package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.service.AdhocService
import io.hamal.lib.sdk.service.DefaultAdhocService
import io.hamal.lib.sdk.service.DefaultExecService
import io.hamal.lib.sdk.service.ExecService

interface HamalSdk {
    fun adhocService(): AdhocService
    fun execService(): ExecService
}

data class DefaultHamalSdk(
    val httpTemplate: HttpTemplate
) : HamalSdk {
    override fun adhocService(): AdhocService {
        return DefaultAdhocService(httpTemplate)
    }

    override fun execService(): ExecService {
        return DefaultExecService(httpTemplate)
    }

}

typealias HttpTemplateSupplier = () -> HttpTemplate