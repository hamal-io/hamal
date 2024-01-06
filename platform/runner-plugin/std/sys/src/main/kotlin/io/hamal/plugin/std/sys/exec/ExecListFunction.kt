package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExecListFunction(
    private val sdk: ApiSdk
) : Function0In2Out<KuaError, KuaArray>(
    FunctionOutput2Schema(KuaError::class, KuaArray::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaArray?> {
        return try {
            val execs = sdk.exec.list(ctx[GroupId::class])
            null to KuaArray(
                execs.mapIndexed { index, exec ->
                    index to KuaMap(
                        mutableMapOf(
                            "id" to KuaString(exec.id.value.value.toString(16)),
                            "status" to KuaString(exec.status.toString()),
                            "correlation_id" to (exec.correlation?.correlationId?.value?.let(::KuaString)
                                ?: KuaNil)
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}