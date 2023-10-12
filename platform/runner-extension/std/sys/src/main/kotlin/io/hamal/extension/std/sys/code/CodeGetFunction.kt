package io.hamal.extension.std.sys.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk


class CodeGetFunctionI(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.code.get(CodeId(arg1.value))
                .let { code ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(code.id.value.value.toString(16)),
                            "code" to CodeType(code.value.value),
                            "version" to StringType(code.version.value.toString(16))
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}

class CodeGetFunctionII(
    private val sdk: ApiSdk
) : Function2In2Out<StringType, NumberType, ErrorType, MapType>(
    FunctionInput2Schema(StringType::class, NumberType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: NumberType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.code.get(CodeId(arg1.value), CodeVersion(arg2.value.toInt()))
                .let { code ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(code.id.value.value.toString(16)),
                            "code" to CodeType(code.value.value),
                            "version" to StringType(code.version.value.toString(16))
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}
