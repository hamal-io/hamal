package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTableMap
import io.hamal.lib.sdk.ApiSdk

class BlueprintGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTableMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTableMap?> {
        return try {
            null to sdk.blueprint.get(BlueprintId(arg1.value))
                .let { bp ->
                    ctx.toMap(
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