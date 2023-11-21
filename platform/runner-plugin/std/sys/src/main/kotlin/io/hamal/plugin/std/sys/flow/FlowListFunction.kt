package io.hamal.plugin.std.sys.flow

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class FlowListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.flow.list(ctx[GroupId::class]).mapIndexed { index, flow ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(flow.id.value.value.toString(16)),
                            "name" to StringType(flow.name.value),
                            "type" to StringType(flow.type.value)
                        )
                    )
                }.toMap().toMutableMap()
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}