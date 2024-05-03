package io.hamal.lib.sdk

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.serialization.json.SerdeModuleJsonDefault
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.lib.http.HttpSerdeJsonFactory
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.HttpTemplateImpl
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
        val serde = Serde.json()
            .register(SerdeModuleJsonApi)
            .register(SerdeModuleJsonDefault)
            .register(SerdeModuleJsonValue)
            .register(SerdeModuleJsonValueVariable)

        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
                this["x-exec-token"] = execToken.stringValue
            },
            serdeFactory = HttpSerdeJsonFactory(serde)
        )
    }

    constructor(apiHost: String, token: AuthToken) {
        val serde = Serde.json()
            .register(SerdeModuleJsonApi)
            .register(SerdeModuleJsonDefault)
            .register(SerdeModuleJsonValue)
            .register(SerdeModuleJsonValueVariable)

        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
            },
            serdeFactory = HttpSerdeJsonFactory(serde)
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