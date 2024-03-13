package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.NamespaceFeatures
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest

class NamespaceAppendFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {

        return try {
            val res = sdk.namespace.append(
                ctx[NamespaceId::class], ApiNamespaceAppendRequest(
                    name = NamespaceName(arg1.getString("name").stringValue),
                    features = NamespaceFeatures.default
                )
            )

            null to ctx.tableCreate(
                "request_id" to KuaString(res.requestId.value.value.toString(16)),
                "request_status" to KuaString(res.requestStatus.name),
                "id" to KuaString(res.id.value.value.toString(16))
            )

        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}