package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiBlueprintCreateRequest

class BlueprintCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {
            val res = sdk.blueprint.create(
                ApiBlueprintCreateRequest(
                    name = BlueprintName(arg1.getString("name").stringValue),
                    inputs = BlueprintInputs(),
                    value = CodeValue(arg1.getString("value").stringValue)
                )
            )

            null to ctx.tableCreate(
                "request_id" to KuaString(res.requestId.value.value.toString(16)),
                "request_status" to KuaString(res.requestStatus.name),
                "id" to KuaString(res.id.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }

    }
}