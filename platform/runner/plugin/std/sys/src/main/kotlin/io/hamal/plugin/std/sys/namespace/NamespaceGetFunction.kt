package io.hamal.plugin.std.sys.namespace

import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.sdk.ApiSdk

class NamespaceGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<ValueString, ValueError, KuaTable>(
    FunctionInput1Schema(ValueString::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString): Pair<ValueError?, KuaTable?> {
        return try {
            null to sdk.namespace.get(NamespaceId(arg1.stringValue))
                .let { namespace ->
                    ctx.tableCreate(
                        "id" to ValueString(namespace.id.value.value.toString(16)),
                        "name" to namespace.name
                    )
                }
        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}