package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findString
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncCreateRequest

class FuncCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.func.create(
                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it.stringValue)) }
                    ?: ctx[NamespaceId::class],
                ApiFuncCreateRequest(
                    name = FuncName(arg1.getString("name")),
                    inputs = FuncInputs(),
                    code = ValueCode(arg1.getString("code").stringValue),
                    codeType = CodeType.Lua54
                )
            )

            null to ctx.tableCreate(
                "request_id" to res.requestId,
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue),
                "workspace_id" to res.workspaceId,
                "namespace_id" to res.namespaceId
            )


        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}