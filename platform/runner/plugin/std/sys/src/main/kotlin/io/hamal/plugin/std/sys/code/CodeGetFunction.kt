package io.hamal.plugin.std.sys.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.value.ValueString


class CodeGetFunction(
    private val sdk: ApiSdk
) : Function2In2Out<ValueString, KuaNumber, KuaError, KuaTable>(
    FunctionInput2Schema(ValueString::class, KuaNumber::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(
        ctx: FunctionContext,
        arg1: ValueString,
        arg2: KuaNumber
    ): Pair<KuaError?, KuaTable?> {
        return try {
            val response = if (arg2 == KuaNumber(-1)) {
                sdk.code.get(CodeId(arg1.stringValue))
            } else {
                sdk.code.get(CodeId(arg1.stringValue), CodeVersion(arg2.intValue))
            }

            null to response
                .let { code ->
                    ctx.tableCreate(
                        "id" to ValueString(code.id.value.value.toString(16)),
                        "code" to KuaCode(code.value.value),
                        "version" to KuaNumber(code.version.value)
                        // FIXME-53 deployed_version ????
                    )
                }
        } catch (t: Throwable) {
            KuaError(t.message!!) to null
        }
    }
}
