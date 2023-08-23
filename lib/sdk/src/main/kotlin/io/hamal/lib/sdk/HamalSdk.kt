package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.service.*

interface HamalSdk {
    fun adhocService(): AdhocService
    val awaitService: AwaitService
    fun execService(): ExecService
    val execLogService: ExecLogService
    val topicService: TopicService
}

data class DefaultHamalSdk(
    val httpTemplate: HttpTemplate
) : HamalSdk {
    override fun adhocService(): AdhocService {
        return DefaultAdhocService(httpTemplate)
    }

    override val awaitService by lazy {
        DefaultAwaitService(httpTemplate)
    }

    override fun execService(): ExecService {
        return DefaultExecService(httpTemplate)
    }

    override val execLogService by lazy {
        DefaultExecLogService(httpTemplate)
    }

    override val topicService by lazy {
        DefaultTopicService(httpTemplate)
    }
}

typealias HttpTemplateSupplier = () -> HttpTemplate