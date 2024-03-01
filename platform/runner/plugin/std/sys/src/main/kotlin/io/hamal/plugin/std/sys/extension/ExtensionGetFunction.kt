package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExtensionGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            val ext = sdk.extension.get(ExtensionId(arg1.value))

            null to ctx.tableCreate(
                "id" to KuaString(ext.id.value.value.toString(16)),
                "name" to KuaString(ext.name.value),
                "code" to ctx.tableCreate(
                    "id" to KuaString(ext.code.id.value.value.toString(16)),
                    "version" to KuaNumber(ext.code.version.value),
                    "value" to KuaCode(ext.code.value.value)
                )
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}