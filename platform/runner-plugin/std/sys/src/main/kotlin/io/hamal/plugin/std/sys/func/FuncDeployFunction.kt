package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.CodeVersion
import io.hamal.lib.domain.vo.DeployMessage
import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFuncDeployRequest

class FuncDeployFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {
        return try {

            val funcId = FuncId(arg1.getString("id"))
            val message = if (arg1.type("message") == StringType::class) {
                DeployMessage(arg1.getString("message"))
            } else {
                null
            }
            val version = if (arg1.type("version") == NumberType::class) {
                CodeVersion(arg1.getInt("version"))
            } else {
                null
            }

            val res = sdk.func.deploy(
                funcId = funcId,
                req = ApiFuncDeployRequest(
                    version = version,
                    message = message
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "func_id" to StringType(res.funcId.value.value.toString(16)),
                )
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}