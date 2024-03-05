package io.hamal.plugin.std.sys.topic

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class TopicResolveFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaString>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaString::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaString?> {
        return try {
            null to KuaString(
                sdk.topic.resolve(ctx[NamespaceId::class], TopicName(arg1.stringValue))
                    .value
                    .value
                    .toString(16)
            )
        } catch (t: Throwable) {
            KuaError(t.message ?: "Unknown Error") to null
        }
    }
}