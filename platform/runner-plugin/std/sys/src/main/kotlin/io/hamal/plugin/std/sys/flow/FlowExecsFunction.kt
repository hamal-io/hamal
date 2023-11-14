package io.hamal.plugin.std.sys.flow

import io.hamal.lib.domain.vo.FlowId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class FlowExecsFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, ArrayType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, ArrayType?> {
        return try {
            val execs = sdk.exec.list(FlowId(arg1.value))
            null to ArrayType(
                execs.mapIndexed { index, exec ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(exec.id.value.value.toString(16)),
                            "status" to StringType(exec.status.toString()),
                            "correlation_id" to (exec.correlation?.correlationId?.value?.let(::StringType) ?: NilType)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}