package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class FuncGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.func.get(FuncId(arg1.stringValue))
                .let { func ->
                    ctx.tableCreate(
                        "id" to KuaString(func.id.value.value.toString(16)),
                        "namespace" to ctx.tableCreate(
                            "id" to KuaString(func.namespace.id.value.value.toString(16)),
                            "name" to KuaString(func.namespace.name.value)
                        ),
                        "name" to KuaString(func.name.value),
                        "code" to ctx.tableCreate(
                            "id" to KuaString(func.code.id.value.value.toString(16)),
                            "version" to KuaNumber(func.code.version.value),
                            "value" to KuaCode(func.code.value.value)
                        ),
                        "deployment" to ctx.tableCreate(
                            "id" to KuaString(func.deployment.id.value.value.toString(16)),
                            "version" to KuaNumber(func.deployment.version.value),
                            "value" to KuaCode(func.deployment.value.value),
                            "message" to KuaString(func.deployment.message.value)
                        ),
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}