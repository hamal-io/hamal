package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString

class NamespaceGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, KuaError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<KuaError?, KuaTable?> {
        return try {
            null to sdk.namespace.get(NamespaceId(arg1.stringValue))
                .let { namespace ->
                    ctx.tableCreate(
                        "id" to ValueString(namespace.id.value.value.toString(16)),
                        "name" to ValueString(namespace.name.value)
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}