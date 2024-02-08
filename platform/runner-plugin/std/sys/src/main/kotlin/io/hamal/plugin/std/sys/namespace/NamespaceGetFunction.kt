package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class NamespaceGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            null to sdk.namespace.get(NamespaceId(arg1.value))
                .let { namespace ->
                    KuaMap(
                        mutableMapOf(
                            "id" to KuaString(namespace.id.value.value.toString(16)),
                            "name" to KuaString(namespace.name.value),
                            "type" to KuaString(namespace.type.value)
                        )
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}