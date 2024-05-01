package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class FuncDeploymentsFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.func.listDeployments(FuncId(arg1.stringValue))
                    .map { deployed ->
                        ctx.tableCreate(
                            "version" to ValueNumber(deployed.version.value),
                            "message" to deployed.message,
                            "deployed_at" to ValueString(deployed.deployedAt.toString())
                        )
                    }
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}