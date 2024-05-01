package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueCode
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString

class ExtensionGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            val ext = sdk.extension.get(ExtensionId(arg1.stringValue))

            null to ctx.tableCreate(
                "id" to ValueString(ext.id.value.value.toString(16)),
                "name" to ValueString(ext.name.value),
                "code" to ctx.tableCreate(
                    "id" to ValueString(ext.code.id.value.value.toString(16)),
                    "version" to ValueNumber(ext.code.version.value),
                    "value" to ValueCode(ext.code.value.value)
                )
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}