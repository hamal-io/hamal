package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExecListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            val execs = sdk.exec.list(ctx[GroupId::class])
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