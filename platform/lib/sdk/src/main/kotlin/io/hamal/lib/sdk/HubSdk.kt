package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.hub.*

interface HubSdk {
    val account: HubAccountService
    val auth: HubAuthService
    val adhoc: HubAdhocService
    val await: HubAwaitService
    val exec: HubExecService
    val execLog: HubExecLogService
    val func: HubFuncService
    val group: HubGroupService
    val topic: HubTopicService
}

data class DefaultHubSdk(
    val httpTemplate: HttpTemplate
) : HubSdk {

    override val account: HubAccountService by lazy {
        DefaultHubAccountService(httpTemplate)
    }

    override val adhoc: HubAdhocService by lazy {
        DefaultHubAdhocService(httpTemplate)
    }

    override val auth: HubAuthService by lazy {
        DefaultHubAuthService(httpTemplate)
    }

    override val await: HubAwaitService by lazy {
        DefaultHubAwaitService(httpTemplate)
    }

    override val exec: HubExecService by lazy {
        DefaultHubExecService(httpTemplate)
    }

    override val execLog: HubExecLogService by lazy {
        DefaultHubExecLogService(httpTemplate)
    }

    override val func: HubFuncService by lazy {
        DefaultHubFuncService(httpTemplate)
    }

    override val group: HubGroupService by lazy {
        DefaultHubGroupService(httpTemplate)
    }

    override val topic: HubTopicService by lazy {
        DefaultHubTopicService(httpTemplate)
    }
}