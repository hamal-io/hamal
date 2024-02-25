package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.toMap
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncDeployRequest

class FuncDeployFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable.Map, KuaError, KuaTable.Map>(
    FunctionInput1Schema(KuaTable.Map::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Map::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable.Map): Pair<KuaError?, KuaTable.Map?> {
        return try {

            val funcId = FuncId(arg1.getString("id"))
            val message = if (arg1.type("message") == KuaString::class) {
                DeployMessage(arg1.getString("message"))
            } else {
                null
            }
            val version = if (arg1.type("version") == KuaNumber::class) {
                CodeVersion(arg1.getInt("version"))
            } else {
                null
            }

            val res = sdk.func.deploy(
                funcId = funcId,
                req = ApiFuncDeployRequest(
                    version = version,
                    message = message
                )
            )

            null to ctx.toMap(
                "id" to KuaString(res.id.value.value.toString(16)),
                "status" to KuaString(res.status.name),
                "func_id" to KuaString(res.funcId.value.value.toString(16)),
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}