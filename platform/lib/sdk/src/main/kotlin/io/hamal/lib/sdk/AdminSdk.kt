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

data class DefaultAdminSdk(
    val template: HttpTemplate
) : AdminSdk {

    override val account: AdminAccountService by lazy {
        DefaultAdminAccountService(template)
    }

    override val adhoc: AdminAdhocService by lazy {
        DefaultAdminAdhocService(template)
    }

    override val auth: AdminAuthService by lazy {
        DefaultAdminAuthService(template)
    }

    override val await: AdminAwaitService by lazy {
        DefaultAdminAwaitService(template)
    }

    override val exec: AdminExecService by lazy {
        DefaultAdminExecService(template)
    }

    override val execLog: AdminExecLogService by lazy {
        DefaultAdminExecLogService(template)
    }

    override val func: AdminFuncService by lazy {
        DefaultAdminFuncService(template)
    }

    override val group: AdminGroupService by lazy {
        DefaultAdminGroupService(template)
    }

    override val namespace: AdminNamespaceService by lazy {
        DefaultAdminNamespaceService(template)
    }

    override val topic: AdminTopicService by lazy {
        DefaultAdminTopicService(template)
    }

    override val trigger: AdminTriggerService by lazy {
        DefaultAdminTriggerService(template)
    }
}