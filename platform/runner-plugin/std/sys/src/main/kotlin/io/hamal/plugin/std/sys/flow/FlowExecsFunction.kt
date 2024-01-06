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
) : Function1In2Out<KuaString, KuaError, KuaArray>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaArray?> {
        return try {
            null to KuaArray(
                sdk.exec.list(FlowId(arg1.value))
                    .mapIndexed { index, exec ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(exec.id.value.value.toString(16)),
                            "status" to KuaString(exec.status.toString()),
                            "correlation_id" to (exec.correlation?.correlationId?.value?.let(::KuaString) ?: KuaNil)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}