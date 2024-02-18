package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiBlueprintUpdateRequest

class BlueprintUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaMap, KuaError, KuaMap>(
    FunctionInput1Schema(KuaMap::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaMap): Pair<KuaError?, KuaMap?> {
        return try {
            val res = sdk.blueprint.update(
                BlueprintId(arg1.getString("id")),
                ApiBlueprintUpdateRequest(
                    name = arg1.findString("name")?.let { BlueprintName(it) },
                    inputs = BlueprintInputs(),
                    value = arg1.findString("value")?.let { CodeValue(it) }
                )
            )

            null to KuaMap(
                mutableMapOf(
                    "id" to KuaString(res.id.value.value.toString(16)),
                    "status" to KuaString(res.status.name),
                    "blueprint_id" to KuaString(res.blueprintId.value.value.toString(16))
                )
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}
