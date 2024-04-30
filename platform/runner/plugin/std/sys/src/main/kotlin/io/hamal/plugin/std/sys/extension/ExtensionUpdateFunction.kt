package io.hamal.plugin.std.sys.extension

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.ExtensionId
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaTable
import io.hamal.lib.kua.type.findString
import io.hamal.lib.kua.type.getString
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiExtensionUpdateRequest
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueString

class ExtensionUpdateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        return try {
            val res = sdk.extension.update(
                ExtensionId(SnowflakeId(arg1.getString("id").stringValue)),
                ApiExtensionUpdateRequest(
                    name = arg1.findString("name")?.let { ExtensionName(it.stringValue) },
                    code = arg1.findString("code")?.let { CodeValue(it.stringValue) }
                )
            )

            null to ctx.tableCreate(
                "request_id" to ValueString(res.requestId.value.value.toString(16)),
                "request_status" to ValueString(res.requestStatus.name),
                "id" to ValueString(res.id.value.value.toString(16)),
            )

        } catch (t: Throwable) {
            ValueError(t.message!!) to null
        }
    }
}
