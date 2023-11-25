package io.hamal.lib.sdk

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.*

interface ApiSdk {
    val account: ApiAccountService
    val auth: AuthService
    val adhoc: ApiAdhocService
    val await: ApiAwaitService
    val code: ApiCodeService
    val endpoint: ApiEndpointService
    val exec: ApiExecService
    val execLog: ApiExecLogService
    val extension: ApiExtensionService
    val func: ApiFuncService
    val group: ApiGroupService
    val hook: ApiHookService
    val flow: ApiFlowService
    val blueprint: ApiBlueprintService
    val topic: ApiTopicService
    val trigger: ApiTriggerService
}

class ApiSdkImpl : ApiSdk {

    constructor(apiHost: String) {
        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
            },
            serdeFactory = {}
        )
    }

    constructor(apiHost: String, token: AuthToken) {
        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
            },
            serdeFactory = {}
        )
    }

    constructor(template: HttpTemplate) {
        this.template = template
    }

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

    override val endpoint: ApiEndpointService by lazy {
        ApiEndpointServiceImpl(template)
    }

    override val exec: ApiExecService by lazy {
        ApiExecServiceImpl(template)
    }

    override val execLog: ApiExecLogService by lazy {
        ApiExecLogServiceImpl(template)
    }

    override val extension: ApiExtensionService by lazy {
        ApiExtensionServiceImpl(template)
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

    override val flow: ApiFlowService by lazy {
        ApiFlowServiceImpl(template)
    }

    override val blueprint: ApiBlueprintService by lazy {
        ApiBlueprintServiceImpl(template)
    }

    override val topic: ApiTopicService by lazy {
        ApiTopicServiceImpl(template)
    }

    override val trigger: ApiTriggerService by lazy {
        ApiTriggerServiceImpl(template)
    }

    val template: HttpTemplate
}