package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.hub.*

interface HubSdk {
    val account: AccountService
    val auth: AuthService
    val adhoc: AdhocService
    val await: AwaitService
    val exec: ExecService
    val execLog: ExecLogService
    val func: FuncService
    val group: GroupService
    val topic: TopicService
}

data class DefaultHubSdk(
    val httpTemplate: HttpTemplate
) : HubSdk {

    override val account: AccountService by lazy {
        DefaultAccountService(httpTemplate)
    }

    override val adhoc: AdhocService by lazy {
        DefaultAdhocService(httpTemplate)
    }

    override val auth: AuthService by lazy {
        DefaultAuthService(httpTemplate)
    }

    override val await: AwaitService by lazy {
        DefaultAwaitService(httpTemplate)
    }

    override val exec: ExecService by lazy {
        DefaultExecService(httpTemplate)
    }

    override val execLog: ExecLogService by lazy {
        DefaultExecLogService(httpTemplate)
    }

    override val func: FuncService by lazy {
        DefaultFuncService(httpTemplate)
    }

    override val group: GroupService by lazy {
        DefaultGroupService(httpTemplate)
    }

    override val topic: TopicService by lazy {
        DefaultTopicService(httpTemplate)
    }
}