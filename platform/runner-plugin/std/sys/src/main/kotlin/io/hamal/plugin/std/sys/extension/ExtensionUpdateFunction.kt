package io.hamal.plugin.std.sys.extension

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExtensionUpdateRequest

class ExtensionUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.extension.update(
                ExtensionId(SnowflakeId(arg1.getString("id"))),
                ApiExtensionUpdateRequest(
                    name = arg1.findString("name")?.let { ExtensionName(it) },
                    code = arg1.findString("code")?.let { CodeValue(it) }
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "extension_id" to StringType(res.extensionId.value.value.toString(16)),
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
