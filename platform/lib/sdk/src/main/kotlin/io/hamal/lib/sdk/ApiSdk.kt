package io.hamal.lib.sdk

import io.hamal.lib.common.serialization.serde.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.JsonHttpSerdeFactory
import io.hamal.lib.sdk.api.*

interface ApiSdk {
    val account: ApiAccountService
    val auth: AuthService
    val adhoc: ApiAdhocService
    val await: ApiAwaitService
    val code: ApiCodeService
    val exec: ApiExecService
    val execLog: ApiExecLogService
    val extension: ApiExtensionService
    val func: ApiFuncService
    val workspace: ApiWorkspaceService
    val namespace: ApiNamespaceService
    val recipe: ApiRecipeService
    val topic: ApiTopicService
    val trigger: ApiTriggerService
}

class ApiSdkImpl : ApiSdk {

    constructor(
        apiHost: String,
        token: AuthToken,
        execToken: ExecToken
    ) {
        val json = Json(
            JsonFactoryBuilder()
                .register(ApiJsonModule)
                .register(HotObjectModule)
                .register(ValueVariableJsonModule)
        )

        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
                this["x-exec-token"] = execToken.stringValue
            },
            serdeFactory = JsonHttpSerdeFactory(json)
        )
    }


    constructor(apiHost: String, token: AuthToken) {
        val json = Json(
            JsonFactoryBuilder()
                .register(ApiJsonModule)
                .register(HotObjectModule)
                .register(ValueVariableJsonModule)
        )

        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
            },
            serdeFactory = JsonHttpSerdeFactory(json)
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

    override val workspace: ApiWorkspaceService by lazy {
        ApiWorkspaceServiceImpl(template)
    }

    override val namespace: ApiNamespaceService by lazy {
        ApiNamespaceServiceImpl(template)
    }

    override val recipe: ApiRecipeService by lazy {
        ApiRecipeServiceImpl(template)
    }

    override val topic: ApiTopicService by lazy {
        ApiTopicServiceImpl(template)
    }

    override val trigger: ApiTriggerService by lazy {
        ApiTriggerServiceImpl(template)
    }

    val template: HttpTemplate
}