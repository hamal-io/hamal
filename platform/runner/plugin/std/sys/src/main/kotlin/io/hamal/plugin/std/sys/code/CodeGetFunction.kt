package io.hamal.plugin.std.sys.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk


class CodeGetFunction(
    private val sdk: ApiSdk
) : Function2In2Out<KuaString, KuaNumber, KuaError, KuaTable.Map>(
    FunctionInput2Schema(KuaString::class, KuaNumber::class),
    FunctionOutput2Schema(KuaError::class, KuaTable.Map::class)
) {
    override fun invoke(
        ctx: FunctionContext,
        arg1: KuaString,
        arg2: KuaNumber
    ): Pair<KuaError?, KuaTable.Map?> {
        return try {
            val response = if (arg2 == KuaNumber(-1)) {
                sdk.code.get(CodeId(arg1.value))
            } else {
                sdk.code.get(CodeId(arg1.value), CodeVersion(arg2.value.toInt()))
            }

            null to response
                .let { code ->
                    KuaTable.Map(
                        "id" to KuaString(code.id.value.value.toString(16)),
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
