package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiBlueprintCreateReq

class BlueprintCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.blueprint.create(
                ctx[GroupId::class],
                ApiBlueprintCreateReq(
                    name = BlueprintName(arg1.getString("name")),
                    inputs = BlueprintInputs(),
                    value = CodeValue(arg1.getString("value"))
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "blueprint_id" to StringType(res.blueprintId.value.value.toString(16)),
                    "group_id" to StringType(res.groupId.value.value.toString(16)),
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }

    }
}