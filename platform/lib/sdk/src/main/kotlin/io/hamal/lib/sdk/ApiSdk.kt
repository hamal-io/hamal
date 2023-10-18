package io.hamal.lib.sdk

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.api.*

interface ApiSdk {
    val account: ApiAccountService
    val auth: AuthService
    val adhoc: ApiAdhocService
    val await: ApiAwaitService
    val code: ApiCodeService
    val exec: ApiExecService
    val execLog: ApiExecLogService
    val func: ApiFuncService
    val group: ApiGroupService
    val hook: ApiHookService
    val namespace: ApiNamespaceService
    val snippet: ApiSnippetService
    val topic: ApiTopicService
    val trigger: ApiTriggerService
}

data class ApiSdkImpl(
    val template: HttpTemplate
) : ApiSdk {

    override val account: ApiAccountService by lazy {
        ApiAccountServiceImpl(template)
    }

    override val adhoc: ApiAdhocService by lazy {
        ApiAdhocServiceImpl(template)
    }

    override val auth: AuthService by lazy {
        ApiAuthServiceImpl(template)
    }

    override val await: ApiAwaitService by lazy {
        ApiAwaitServiceImpl(template)
    }

    override val code: ApiCodeService by lazy {
        ApiCodeServiceImpl(template)
    }

    override val exec: ApiExecService by lazy {
        ApiExecServiceImpl(template)
    }

    override val execLog: ApiExecLogService by lazy {
        ApiExecLogServiceImpl(template)
    }

    override val func: ApiFuncService by lazy {
        ApiFuncServiceImpl(template)
    }

    override val group: ApiGroupService by lazy {
        ApiGroupServiceImpl(template)
    }

    override val hook: ApiHookService by lazy {
        ApiHookServiceImpl(template)
    }

    override val namespace: ApiNamespaceService by lazy {
        ApiNamespaceServiceImpl(template)
    }

    override val snippet: ApiSnippetService by lazy {
        ApiSnippetServiceImpl(template)
    }

    override val topic: ApiTopicService by lazy {
        ApiTopicServiceImpl(template)
    }

    override val trigger: ApiTriggerService by lazy {
        ApiTriggerServiceImpl(template)
    }
}