package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class FuncGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            null to sdk.func.get(FuncId(arg1.stringValue))
                .let { func ->
                    ctx.tableCreate(
                        "id" to ValueString(func.id.value.value.toString(16)),
                        "namespace" to ctx.tableCreate(
                            "id" to ValueString(func.namespace.id.value.value.toString(16)),
                            "name" to ValueString(func.namespace.name.value)
                        ),
                        "name" to func.name.value,
                        "code" to ctx.tableCreate(
                            "id" to ValueString(func.code.id.value.value.toString(16)),
                            "version" to ValueNumber(func.code.version.value),
                            "value" to ValueCode(func.code.value.value)
                        ),
                        "deployment" to ctx.tableCreate(
                            "id" to ValueString(func.deployment.id.value.value.toString(16)),
                            "version" to ValueNumber(func.deployment.version.value),
                            "value" to ValueCode(func.deployment.value.value),
                            "message" to ValueString(func.deployment.message.value)
                        ),
                    )
                }
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}