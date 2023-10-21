package io.hamal.extension.unsafe.std.sys.func

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class HookListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.hook.list(ctx[GroupId::class])
                    .mapIndexed { index, func ->
                        index to MapType(
                            mutableMapOf(
                                "id" to StringType(func.id.value.value.toString(16)),
                                "namespace" to MapType(
                                    mutableMapOf(
                                        "id" to StringType(func.namespace.id.value.value.toString(16)),
                                        "name" to StringType(func.namespace.name.value)
                                    )
                                ),
                                "name" to StringType(func.name.value),
                            )
                        )
                    }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}