package io.hamal.plugin.std.sys.extension

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExtensionId.Companion.ExtensionId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

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
                "id" to ValueString(ext.id.stringValue),
                "name" to ext.name,
                "code" to ctx.tableCreate(
                    "id" to ValueString(ext.code.id.stringValue),
                    "version" to ext.code.version,
                    "value" to ext.code.value
                )
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}