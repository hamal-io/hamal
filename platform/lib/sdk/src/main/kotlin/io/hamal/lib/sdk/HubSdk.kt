package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.hub.service.*

interface HubSdk {
    val accountService: AccountService
    val authService: AuthService
    val adhocService: AdhocService
    val awaitService: AwaitService
    val execService: ExecService
    val execLogService: ExecLogService
    val groupService: GroupService
    val topicService: TopicService
}

data class DefaultHubSdk(
    val httpTemplate: HttpTemplate
) : HubSdk {

    override val accountService: AccountService by lazy {
        DefaultAccountService(httpTemplate)
    }

    override val adhocService: AdhocService by lazy {
        DefaultAdhocService(httpTemplate)
    }

    override val authService: AuthService by lazy {
        DefaultAuthService(httpTemplate)
    }

    override val awaitService: AwaitService by lazy {
        DefaultAwaitService(httpTemplate)
    }

    override val execService: ExecService by lazy {
        DefaultExecService(httpTemplate)
    }

    override val execLogService: ExecLogService by lazy {
        DefaultExecLogService(httpTemplate)
    }
    override val groupService: GroupService by lazy {
        DefaultGroupService(httpTemplate)
    }

    override val topicService: TopicService by lazy {
        DefaultTopicService(httpTemplate)
    }
}