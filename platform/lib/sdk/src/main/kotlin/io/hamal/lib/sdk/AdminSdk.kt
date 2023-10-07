package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.admin.*

interface AdminSdk {
    val account: AdminAccountService
    val auth: AdminAuthService
    val adhoc: AdminAdhocService
    val await: AdminAwaitService
    val exec: AdminExecService
    val execLog: AdminExecLogService
    val func: AdminFuncService
    val group: AdminGroupService
    val namespace: AdminNamespaceService
    val topic: AdminTopicService
    val trigger: AdminTriggerService
}

data class AdminSdkImpl(
    val template: HttpTemplate
) : AdminSdk {

    override val account: AdminAccountService by lazy {
        AdminAccountServiceImpl(template)
    }

    override val adhoc: AdminAdhocService by lazy {
        AdminAdhocServiceImpl(template)
    }

    override val auth: AdminAuthService by lazy {
        AdminAuthServiceImpl(template)
    }

    override val await: AdminAwaitService by lazy {
        AdminAwaitServiceImpl(template)
    }

    override val exec: AdminExecService by lazy {
        AdminExecServiceImpl(template)
    }

    override val execLog: AdminExecLogService by lazy {
        AdminExecLogServiceImpl(template)
    }

    override val func: AdminFuncService by lazy {
        AdminFuncServiceImpl(template)
    }

    override val group: AdminGroupService by lazy {
        AdminGroupServiceImpl(template)
    }

    override val namespace: AdminNamespaceService by lazy {
        AdminNamespaceServiceImpl(template)
    }

    override val topic: AdminTopicService by lazy {
        AdminTopicServiceImpl(template)
    }

    override val trigger: AdminTriggerService by lazy {
        AdminTriggerServiceImpl(template)
    }
}