package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.findString
import io.hamal.lib.kua.value.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiNamespaceUpdateRequest

class NamespaceUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.namespace.update(
                NamespaceId(arg1.getString("id").stringValue),
                ApiNamespaceUpdateRequest(
                    name = arg1.findString("name")?.let { NamespaceName(it) },
                    features = null
                )
            )

            null to ctx.tableCreate(
                "request_id" to res.requestId,
                "request_status" to ValueString(res.requestStatus.stringValue),
                "id" to ValueString(res.id.stringValue)
            )
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}