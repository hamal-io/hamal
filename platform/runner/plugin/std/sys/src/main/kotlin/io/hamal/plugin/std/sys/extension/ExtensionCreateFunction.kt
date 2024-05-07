package io.hamal.plugin.std.sys.extension

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.CodeValue.Companion.CodeValue
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.domain.vo.WorkspaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExtensionCreateRequest

class ExtensionCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.extension.create(
                ctx[WorkspaceId::class],
                ApiExtensionCreateRequest(
                    name = ExtensionName(arg1.getString("name")),
                    code = CodeValue(arg1.getString("code").stringValue)
                )
            )

            null to ctx.tableCreate(
                "request_id" to res.requestId,
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to res.id,
                "workspace_id" to res.workspaceId
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }

}