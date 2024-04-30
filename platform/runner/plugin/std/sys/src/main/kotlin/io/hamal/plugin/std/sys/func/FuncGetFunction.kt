package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString

class FuncGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, KuaError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.func.get(FuncId(arg1.stringValue))
                .let { func ->
                    ctx.tableCreate(
                        "id" to ValueString(func.id.value.value.toString(16)),
                        "namespace" to ctx.tableCreate(
                            "id" to ValueString(func.namespace.id.value.value.toString(16)),
                            "name" to ValueString(func.namespace.name.value)
                        ),
                        "name" to ValueString(func.name.value),
                        "code" to ctx.tableCreate(
                            "id" to ValueString(func.code.id.value.value.toString(16)),
                            "version" to ValueNumber(func.code.version.value),
                            "value" to KuaCode(func.code.value.value)
                        ),
                        "deployment" to ctx.tableCreate(
                            "id" to ValueString(func.deployment.id.value.value.toString(16)),
                            "version" to ValueNumber(func.deployment.version.value),
                            "value" to KuaCode(func.deployment.value.value),
                            "message" to ValueString(func.deployment.message.value)
                        ),
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}