package io.hamal.plugin.std.sys.extension

import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExtensionGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            val ext = sdk.extension.get(ExtensionId(arg1.value))

            val res = mutableMapOf(
                "id" to StringType(ext.id.value.value.toString(16)),
                "name" to StringType(ext.name.value),
                "code" to MapType(
                    mutableMapOf(
                        "id" to StringType(ext.code.id.value.value.toString(16)),
                        "version" to NumberType(ext.code.version.value),
                        "value" to CodeType(ext.code.value.value)
                    )
                )
            )
            null to MapType(res)

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}