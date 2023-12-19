package io.hamal.plugin.std.sys.func

import io.hamal.lib.domain.vo.FuncId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class FuncDeploymentsFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, ArrayType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.func.listDeployments(
                    FuncId(arg1.value)
                ).mapIndexed { idx, deployed ->
                    idx to MapType(
                        mutableMapOf(
                            "version" to NumberType(deployed.version.value),
                            "message" to StringType(deployed.message.value),
                            "deployed_at" to StringType(deployed.deployedAt.toString())
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}