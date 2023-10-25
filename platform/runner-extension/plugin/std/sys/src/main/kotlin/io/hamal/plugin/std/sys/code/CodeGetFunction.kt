package io.hamal.plugin.std.sys.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk


class CodeGetFunction(
    private val sdk: ApiSdk
) : Function2In2Out<StringType, NumberType, ErrorType, MapType>(
    FunctionInput2Schema(StringType::class, NumberType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: NumberType): Pair<ErrorType?, MapType?> {
        return try {
            val response = if (arg2 == NumberType(-1)) {
                sdk.code.get(CodeId(arg1.value))
            } else {
                sdk.code.get(CodeId(arg1.value), CodeVersion(arg2.value.toInt()))
            }

            null to response
                .let { code ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(code.id.value.value.toString(16)),
                            "code" to CodeType(code.value.value),
                            "version" to NumberType(code.version.value)
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
