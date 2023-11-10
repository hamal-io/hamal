package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class FuncGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            null to sdk.func.get(FuncId(arg1.value))
                .let { func ->
                    MapType(
                        mutableMapOf(
                            "id" to StringType(func.id.value.value.toString(16)),
                            "flow" to MapType(
                                mutableMapOf(
                                    "id" to StringType(func.flow.id.value.value.toString(16)),
                                    "name" to StringType(func.flow.name.value)
                                )
                            ),
                            "name" to StringType(func.name.value),
                            "code" to MapType(
                                mutableMapOf(
                                    "id" to StringType(func.code.id.value.value.toString(16)),
                                    "current" to MapType(
                                        "version" to NumberType(func.code.current.version.value),
                                        "value" to CodeType(func.code.current.value.value),
                                    ),
                                    "deployed" to MapType(
                                        "version" to NumberType(func.code.deployed.version.value),
                                        "value" to CodeType(func.code.deployed.value.value),
                                    ),
                                )
                            )
                        )
                    )
                }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}