package io.hamal.plugin.std.sys.blueprint

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiUpdateBlueprintReq

class BlueprintUpdateFunction(
    private val sdk: ApiSdk
) : Function2In2Out<StringType, MapType, ErrorType, MapType>(
    FunctionInput2Schema(StringType::class, MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.blueprint.update(
                BlueprintId(arg1.value),
                ApiUpdateBlueprintReq(
                    name = BlueprintName(arg2.getString("name")),
                    inputs = BlueprintInputs(),
                    value = CodeValue(arg2.getString("value"))
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "blueprint_id" to StringType(res.blueprintId.value.value.toString(16))
                )
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
