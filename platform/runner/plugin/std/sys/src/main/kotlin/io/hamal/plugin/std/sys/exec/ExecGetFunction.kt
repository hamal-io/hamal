package io.hamal.plugin.std.sys.exec

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNil
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class ExecGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            val exec = sdk.exec.get(ExecId(arg1.stringValue))
            null to ctx.tableCreate(
                "id" to ValueString(exec.id.stringValue),
                "status" to exec.status,
                "inputs" to ctx.tableCreate(),
                "correlation" to ctx.tableCreate(
                    "id" to (exec.correlation?.value ?: ValueNil)
                )
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}