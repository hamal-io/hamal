package io.hamal.extension.std.sysadmin.func

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.AdminSdk
import io.hamal.lib.sdk.admin.AdminCreateFuncReq

class CreateFuncFunction(
    private val sdk: AdminSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {
            val namespaceId = if (arg1.type("namespace_id") == StringType::class) {
                NamespaceId(SnowflakeId(arg1.getString("namespace_id")))
            } else {
                null
            }

            val res = sdk.func.create(
                ctx[GroupId::class],
                AdminCreateFuncReq(
                    namespaceId = namespaceId,
                    name = FuncName(arg1.getString("name")),
                    inputs = FuncInputs(),
                    code = arg1.getCodeType("code")
                )
            )

            null to MapType(
                mutableMapOf(
                    "req_id" to StringType(res.reqId.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "id" to StringType(res.id.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}