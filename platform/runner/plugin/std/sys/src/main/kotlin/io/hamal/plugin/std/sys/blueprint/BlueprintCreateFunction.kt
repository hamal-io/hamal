package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTableMap
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest

class BlueprintCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTableMap, KuaError, KuaTableMap>(
    FunctionInput1Schema(KuaTableMap::class),
    FunctionOutput2Schema(KuaError::class, KuaTableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTableMap): Pair<KuaError?, KuaTableMap?> {
        return try {
            val res = sdk.blueprint.create(
                ApiBlueprintCreateRequest(
                    name = BlueprintName(arg1.getString("name")),
                    inputs = BlueprintInputs(),
                    value = CodeValue(arg1.getString("value"))
                )
            )

            null to ctx.toMap(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "blueprint_id" to KuaString(res.blueprintId.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }

    }
}