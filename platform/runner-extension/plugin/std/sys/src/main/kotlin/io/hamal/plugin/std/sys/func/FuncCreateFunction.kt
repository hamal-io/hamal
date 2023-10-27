package io.hamal.plugin.std.sys.func

import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiCreateFuncReq

class FuncCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val res = sdk.func.create(
                arg1.findString("namespace_id")?.let { NamespaceId(SnowflakeId(it)) } ?: ctx[NamespaceId::class],
                ApiCreateFuncReq(
                    name = FuncName(arg1.getString("name")),
                    inputs = FuncInputs(),
                    code = CodeValue(arg1.getString("code"))
                )
            )

            null to MapType(
                mutableMapOf(
                    "req_id" to StringType(res.reqId.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "id" to StringType(res.id.value.toString(16)),
                    "group_id" to StringType(res.groupId.value.value.toString(16)),
                    "namespace_id" to StringType(res.namespaceId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}