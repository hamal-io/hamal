package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk

class FuncDeploymentsFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaString, KuaError, KuaTable>(
    FunctionInput1Schema(KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString): Pair<KuaError?, KuaTable?> {
        return try {
            null to ctx.tableCreate(
                sdk.func.listDeployments(FuncId(arg1.value))
                    .map { deployed ->
                        ctx.tableCreate(
                            "version" to KuaNumber(deployed.version.value),
                            "message" to KuaString(deployed.message.value),
                            "deployed_at" to KuaString(deployed.deployedAt.toString())
                        )
                    }
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}