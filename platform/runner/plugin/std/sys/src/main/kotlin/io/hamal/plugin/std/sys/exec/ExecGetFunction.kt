package io.hamal.plugin.std.sys.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaMap
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.ApiSdk

class ExecGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaMap>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaMap?> {
        return try {
            val exec = sdk.exec.get(ExecId(arg1.value))
            null to KuaMap().also {
                it["id"] = exec.id.value.value.toString(16)
                it["status"] = KuaString(exec.status.name)
                it["inputs"] = KuaMap() // FIXME
                exec.correlation?.value?.let { corId ->
                    it["correlation_id"] = corId
                } // FIXME set nil value to table --> makes the api nicer
            }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}