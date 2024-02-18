package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExtensionGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            val ext = sdk.extension.get(ExtensionId(arg1.value))

            val res = mutableMapOf(
                "id" to KuaString(ext.id.value.value.toString(16)),
                "name" to KuaString(ext.name.value),
                "code" to KuaMap(
                    mutableMapOf(
                        "id" to KuaString(ext.code.id.value.value.toString(16)),
                        "version" to KuaNumber(ext.code.version.value),
                        "value" to KuaCode(ext.code.value.value)
                    )
                )
            )
            null to KuaMap(res)

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}