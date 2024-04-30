package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncDeployRequest
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString

class FuncDeployFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        return try {

            val funcId = FuncId(arg1.getString("id").stringValue)
            val message = if (arg1.type("message") == ValueString::class) {
                DeployMessage(arg1.getString("message").stringValue)
            } else {
                null
            }
            val version = if (arg1.type("version") == ValueNumber::class) {
                CodeVersion(arg1.getNumber("version").intValue)
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

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16)),
            )
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}