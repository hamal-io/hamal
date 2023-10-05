package io.hamal.extension.std.sysadmin.namespace

import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.AdminSdk

class ListNamespaceFunction(
    private val sdk: AdminSdk
) : Function0In2Out<ErrorType, ArrayType>(
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
        return try {
            null to ArrayType(
                sdk.namespace.list().mapIndexed { index, namespace ->
                    index to MapType(
                        mutableMapOf(
                            "id" to StringType(namespace.id.value.value.toString(16)),
                            "name" to StringType(namespace.name.value),
                        )
                    )
                }.toMap().toMutableMap()
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}