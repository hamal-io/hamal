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
    val namespace: HubNamespaceService
    val topic: HubTopicService
}

data class DefaultHubSdk(
    val template: HttpTemplate
) : HubSdk {

    override val account: HubAccountService by lazy {
        DefaultHubAccountService(template)
    }

    override val adhoc: HubAdhocService by lazy {
        DefaultHubAdhocService(template)
    }

    override val auth: HubAuthService by lazy {
        DefaultHubAuthService(template)
    }

    override val await: HubAwaitService by lazy {
        DefaultHubAwaitService(template)
    }

    override val exec: HubExecService by lazy {
        DefaultHubExecService(template)
    }

    override val execLog: HubExecLogService by lazy {
        DefaultHubExecLogService(template)
    }

    override val func: HubFuncService by lazy {
        DefaultHubFuncService(template)
    }

    override val group: HubGroupService by lazy {
        DefaultHubGroupService(template)
    }

    override val namespace: HubNamespaceService by lazy {
        DefaultHubNamespaceService(template)
    }

    override val topic: HubTopicService by lazy {
        DefaultHubTopicService(template)
    }
}