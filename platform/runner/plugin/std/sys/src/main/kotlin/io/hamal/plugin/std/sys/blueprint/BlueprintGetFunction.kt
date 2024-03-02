package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class BlueprintGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.blueprint.get(BlueprintId(arg1.stringValue))
                .let { bp ->
                    ctx.tableCreate(
                        "id" to KuaString(bp.id.value.value.toString(16)),
                        "name" to KuaString(bp.name.value),
                        "value" to KuaCode(bp.value.value)
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}