package io.hamal.extension.std.sysadmin.exec

import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.AdminSdk

class ListExecFunction(
    private val sdk: AdminSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            val execs = sdk.exec.list()
            null to ArrayType(
                execs.mapIndexed { index, exec ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(exec.id.value.value.toString(16)),
                            "status" to StringType(exec.status.toString())
                        )
                    )
                }.toMap().toMutableMap()
            )
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}