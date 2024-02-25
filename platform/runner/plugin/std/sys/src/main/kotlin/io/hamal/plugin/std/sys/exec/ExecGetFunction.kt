package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class ExecGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable.Map>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Map::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable.Map?> {
        return try {
            val exec = sdk.exec.get(ExecId(arg1.value))
            null to ctx.toMap(
                "id" to KuaString(exec.id.value.value.toString(16)),
                "status" to KuaString(exec.status.name)
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}