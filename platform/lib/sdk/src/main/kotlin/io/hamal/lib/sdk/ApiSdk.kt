package io.hamal.lib.sdk

import com.google.gson.reflect.TypeToken
import io.hamal.lib.common.hot.HotJsonModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ValueObjectJsonModule
import io.hamal.lib.http.HttpContentDeserializer
import io.hamal.lib.http.HttpContentSerializer
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.api.*
import java.io.InputStream
import kotlin.reflect.KClass

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
    val workspace: ApiWorkspaceService
    val hook: ApiHookService
    val namespace: ApiNamespaceService
    val blueprint: ApiBlueprintService
    val topic: ApiTopicService
    val trigger: ApiTriggerService
}

class ApiSdkImpl : ApiSdk {

    constructor(apiHost: String, token: AuthToken) {
        val json = Json(
            JsonFactoryBuilder()
                .register(ApiJsonModule)
                .register(HotJsonModule)
                .register(ValueObjectJsonModule)
        )

        template = HttpTemplateImpl(
            baseUrl = apiHost,
            headerFactory = {
                this["accept"] = "application/json"
                this["authorization"] = "Bearer ${token.value}"
            },
            serdeFactory = {
                contentDeserializer = object : HttpContentDeserializer {
                    override fun <VALUE : Any> deserialize(inputStream: InputStream, clazz: KClass<VALUE>): VALUE {
                        return json.deserialize(clazz, inputStream)
                    }

                    override fun <VALUE : Any> deserializeList(
                        inputStream: InputStream,
                        clazz: KClass<VALUE>
                    ): List<VALUE> {
                        return json.deserialize(object : TypeToken<List<VALUE>>() {}, inputStream)
                    }
                }
                contentSerializer = object : HttpContentSerializer {
                    override fun <VALUE : Any> serialize(value: VALUE, clazz: KClass<VALUE>): String {
                        return json.serialize(value)
                    }
                }
            }
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

    override val workspace: ApiWorkspaceService by lazy {
        ApiWorkspaceServiceImpl(template)
    }

    override val hook: ApiHookService by lazy {
        ApiHookServiceImpl(template)
    }

    override val namespace: ApiNamespaceService by lazy {
        ApiNamespaceServiceImpl(template)
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